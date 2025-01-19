package com.project.smunionbe.domain.notification.attendance.service.command;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.notification.attendance.converter.AttendanceConverter;
import com.project.smunionbe.domain.notification.attendance.dto.request.AttendanceReqDTO;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceStatus;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceErrorCode;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceException;
import com.project.smunionbe.domain.notification.attendance.repository.AttendanceRepository;
import com.project.smunionbe.domain.notification.attendance.repository.AttendanceStatusRepository;
import com.project.smunionbe.domain.notification.fcm.service.event.FCMNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AttendanceCommandService {

    private final AttendanceRepository attendanceRepository;
    private final ClubRepository clubRepository;
    private final MemberClubRepository memberClubRepository;
    private final AttendanceStatusRepository attendanceStatusRepository;
    private final FCMNotificationService fcmNotificationService;

    public void createAttendance(AttendanceReqDTO.CreateAttendanceDTO request, Long selectedMemberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.MEMBER_NOT_FOUND));

        if (!memberClub.is_Staff()) {
            throw new AttendanceException(AttendanceErrorCode.ACCESS_DENIED);
        }

        Long clubId = memberClub.getClub().getId();

        // 2. 권한 확인
        if (!memberClubRepository.existsByMemberIdAndClubId(memberClub.getMember().getId(), clubId)) {
            throw new AttendanceException(AttendanceErrorCode.ACCESS_DENIED);
        }

        // 3. 동아리 조회
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ACCESS_DENIED));

        // 4. 타겟 부서 멤버 조회
        List<MemberClub> targetMembers = (request.targetDepartments() == null || request.targetDepartments().isEmpty())
                ? memberClubRepository.findAllByClubId(clubId) // 전체 부서
                : memberClubRepository.findAllByClubIdAndDepartments(clubId, request.targetDepartments());

        // 5. 출석 공지 생성
        AttendanceNotice attendanceNotice = AttendanceConverter.toAttendanceNotice(request, club);
        attendanceRepository.save(attendanceNotice);

        // 6. AttendanceStatus 생성
        List<AttendanceStatus> attendanceStatuses = targetMembers.stream()
                .map(targetMember -> AttendanceStatus.builder()
                        .attendanceNotice(attendanceNotice)
                        .memberClub(targetMember)
                        .isPresent(false)
                        .build())
                .toList();
        attendanceStatusRepository.saveAll(attendanceStatuses);

        // 7. FCM 푸시 알림 전송
        fcmNotificationService.sendPushNotifications(attendanceNotice, targetMembers);
    }

    public void updateAttendance(Long attendanceId, AttendanceReqDTO.UpdateAttendanceRequest request, Long selectedMemberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.MEMBER_NOT_FOUND));

        if (!memberClub.is_Staff()) {
            throw new AttendanceException(AttendanceErrorCode.ACCESS_DENIED);
        }

        // 2. AttendanceNotice 조회
        AttendanceNotice attendanceNotice = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));

        // 권한 확인
        if (!memberClub.getClub().equals(attendanceNotice.getClub())) {
            throw new AttendanceException(AttendanceErrorCode.ACCESS_DENIED);
        }

        // 3. 새로운 타겟 멤버 조회
        List<String> targetDepartments = (request.targetDepartments() == null || request.targetDepartments().isEmpty())
                ? List.of("전체") // targetDepartments가 null 또는 비어 있으면 "전체" 설정
                : request.targetDepartments();

        List<MemberClub> newTargetMembers = targetDepartments.contains("전체")
                ? memberClubRepository.findAllByClubId(attendanceNotice.getClub().getId()) // 전체 멤버
                : memberClubRepository.findAllByClubIdAndDepartments(
                attendanceNotice.getClub().getId(),
                targetDepartments
        );

        if (newTargetMembers.isEmpty()) {
            throw new AttendanceException(AttendanceErrorCode.NO_TARGET_MEMBERS);
        }

        // 4. 기존 AttendanceStatus 조회
        List<AttendanceStatus> existingStatuses = attendanceStatusRepository.findAllByAttendanceNotice(attendanceNotice);
        Map<Long, AttendanceStatus> existingStatusMap = existingStatuses.stream()
                .collect(Collectors.toMap(status -> status.getMemberClub().getId(), status -> status));

        // 5. 상태 업데이트 (기존 상태 갱신 + 새로운 상태 추가)
        for (MemberClub member : newTargetMembers) {
            AttendanceStatus status = existingStatusMap.get(member.getId());
            if (status == null) {
                // 새로운 멤버 상태 생성
                attendanceStatusRepository.save(AttendanceStatus.builder()
                        .attendanceNotice(attendanceNotice)
                        .memberClub(member)
                        .isPresent(false) // 초기 상태
                        .build());
            }
        }

        // 6. 유효하지 않은 멤버 상태 삭제
        List<AttendanceStatus> toRemove = existingStatuses.stream()
                .filter(status -> !newTargetMembers.contains(status.getMemberClub()))
                .toList();
        attendanceStatusRepository.deleteAll(toRemove);

        // 7. AttendanceNotice 수정
        attendanceNotice.update(
                request.title(),
                request.content(),
                targetDepartments.contains("전체") ? "전체" : String.join(",", targetDepartments), // "전체" 처리
                request.date()
        );

        // 8. 로그 기록
        log.info("출석 공지가 수정되었습니다. attendanceId: {}, memberClubId: {}", attendanceId, selectedMemberClubId);
    }

    public void deleteAttendance(Long attendanceId, Long selectedMemberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.MEMBER_NOT_FOUND));

        if (!memberClub.is_Staff()) {
            throw new AttendanceException(AttendanceErrorCode.ACCESS_DENIED);
        }

        AttendanceNotice attendanceNotice = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));

        // 권한 확인
        if (!memberClub.getClub().equals(attendanceNotice.getClub())) {
            throw new AttendanceException(AttendanceErrorCode.ACCESS_DENIED);
        }

        attendanceStatusRepository.deleteAllByAttendanceNoticeId(attendanceId);
        attendanceRepository.delete(attendanceNotice);
    }

    public void verifyAttendance(AttendanceReqDTO.VerifyAttendanceRequest request, Long selectedMemberClubId) {
        // MemberClub 조회
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.MEMBER_NOT_FOUND));

        AttendanceNotice attendanceNotice = attendanceRepository.findById(request.attendanceId())
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));

        AttendanceStatus attendanceStatus = attendanceStatusRepository.findByAttendanceAndMemberClub(
                        attendanceNotice.getId(),
                        memberClub.getId()
                )
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ATTENDANCE_STATUS_NOT_FOUND));

        if (attendanceStatus.getIsPresent()) {
            throw new AttendanceException(AttendanceErrorCode.ALREADY_PRESENT);
        }

        attendanceStatus.markPresent();
    }
}
