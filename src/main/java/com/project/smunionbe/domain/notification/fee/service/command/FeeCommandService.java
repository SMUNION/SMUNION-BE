package com.project.smunionbe.domain.notification.fee.service.command;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import com.project.smunionbe.domain.notification.fcm.service.event.FCMNotificationService;
import com.project.smunionbe.domain.notification.fee.converter.FeeNoticeConverter;
import com.project.smunionbe.domain.notification.fee.dto.request.FeeReqDTO;
import com.project.smunionbe.domain.notification.fee.entity.FeeNotice;
import com.project.smunionbe.domain.notification.fee.entity.FeeStatus;
import com.project.smunionbe.domain.notification.fee.exception.FeeErrorCode;
import com.project.smunionbe.domain.notification.fee.exception.FeeException;
import com.project.smunionbe.domain.notification.fee.repository.FeeNoticeRepository;
import com.project.smunionbe.domain.notification.fee.repository.FeeStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeeCommandService {

    private final MemberClubRepository memberClubRepository;
    private final ClubRepository clubRepository;
    private final FeeNoticeRepository feeNoticeRepository;
    private final FeeStatusRepository feeStatusRepository;
    private final FCMNotificationService fcmNotificationService;

    public void createFeeNotice(FeeReqDTO.CreateFeeNoticeRequestDTO request, Long memberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        if (!memberClub.is_Staff()) {
            throw new FeeException(FeeErrorCode.ACCESS_DENIED);
        }

        // 2. 동아리 정보 추출
        Club club = memberClub.getClub();

        // 3. 타겟 멤버 조회
        List<MemberClub> targetMembers = (request.targetDepartments() == null || request.targetDepartments().isEmpty())
                ? memberClubRepository.findAllByClubId(club.getId())
                : memberClubRepository.findAllByClubIdAndDepartments(club.getId(), request.targetDepartments());

        // 4. FeeNotice 생성
        FeeNotice feeNotice = FeeNoticeConverter.toFeeNotice(request, club);
        feeNoticeRepository.save(feeNotice);

        // 5. FeeStatus 생성
        List<FeeStatus> feeStatuses = targetMembers.stream()
                .map(member -> FeeStatus.builder()
                        .feeNotice(feeNotice)
                        .memberClub(member)
                        .isPaid(false)
                        .build())
                .toList();
        feeStatusRepository.saveAll(feeStatuses);

        // 6. FCM 푸시 알림 전송
        fcmNotificationService.sendFeePushNotifications(feeNotice, targetMembers);

        log.info("회비 공지가 생성되었습니다. feeNoticeId: {}, clubId: {}", feeNotice.getId(), club.getId());
    }

    public void updatePaymentStatus(Long feeId, Long memberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        if (!memberClub.is_Staff()) {
            throw new FeeException(FeeErrorCode.ACCESS_DENIED);
        }

        // 2. FeeNotice 조회
        FeeNotice feeNotice = feeNoticeRepository.findById(feeId)
                .orElseThrow(() -> new FeeException(FeeErrorCode.FEE_NOTICE_NOT_FOUND));

        // 3. FeeStatus 업데이트
        FeeStatus feeStatus = feeStatusRepository.findByFeeNoticeAndMemberClub(feeNotice, memberClub)
                .orElseGet(() -> FeeStatus.builder()
                        .feeNotice(feeNotice)
                        .memberClub(memberClub)
                        .build());

        feeStatus.setPaid(true); // 납부 상태 업데이트
        feeStatusRepository.save(feeStatus);

        log.info("회비 납부 상태가 업데이트되었습니다. feeId: {}, memberClubId: {}", feeId, memberClubId);
    }

    public void updateFeeNotice(Long feeId, FeeReqDTO.UpdateFeeNoticeRequest request, Long memberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        if (!memberClub.is_Staff()) {
            throw new FeeException(FeeErrorCode.ACCESS_DENIED);
        }

        // 2. FeeNotice 조회
        FeeNotice feeNotice = feeNoticeRepository.findById(feeId)
                .orElseThrow(() -> new FeeException(FeeErrorCode.FEE_NOTICE_NOT_FOUND));

        // 3. 새로운 타겟 멤버 조회
        List<String> targetDepartments = (request.targetDepartments() == null || request.targetDepartments().isEmpty())
                ? List.of("전체") // targetDepartments가 null 또는 빈 값이면 "전체"를 설정
                : request.targetDepartments();

        List<MemberClub> newTargetMembers = targetDepartments.contains("전체")
                ? memberClubRepository.findAllByClubId(memberClub.getClub().getId()) // 전체 멤버
                : memberClubRepository.findAllByClubIdAndDepartments(
                memberClub.getClub().getId(),
                targetDepartments
        );

        if (newTargetMembers.isEmpty()) {
            throw new FeeException(FeeErrorCode.NO_TARGET_MEMBERS);
        }

        // 4. 기존 FeeStatus 조회
        List<FeeStatus> existingStatuses = feeStatusRepository.findAllByFeeNotice(feeNotice);
        Map<Long, FeeStatus> existingStatusMap = existingStatuses.stream()
                .collect(Collectors.toMap(status -> status.getMemberClub().getId(), status -> status));

        // 5. 상태 업데이트 (기존 상태 갱신 + 새로운 상태 추가)
        for (MemberClub member : newTargetMembers) {
            FeeStatus status = existingStatusMap.get(member.getId());
            if (status == null) {
                // 새로운 멤버 상태 생성
                feeStatusRepository.save(FeeStatus.builder()
                        .feeNotice(feeNotice)
                        .memberClub(member)
                        .isPaid(false) // 초기 상태
                        .build());
            }
        }

        // 6. 유효하지 않은 멤버 상태 삭제
        List<FeeStatus> toRemove = existingStatuses.stream()
                .filter(status -> !newTargetMembers.contains(status.getMemberClub()))
                .toList();
        feeStatusRepository.deleteAll(toRemove);

        // 7. FeeNotice 수정
        feeNotice.update(
                request.title(),
                request.content(),
                request.amount(),
                request.bank(),
                request.accountNumber(),
                request.date(),
                request.participantCount(),
                targetDepartments.contains("전체") ? "전체" : String.join(",", targetDepartments) // "전체" 처리
        );

        // 8. 로그 기록
        log.info("회비 공지가 수정되었습니다. feeId: {}, memberClubId: {}", feeId, memberClubId);
    }

    public void deleteFeeNotice(Long feeId, Long memberClubId) {
        // 1. MemberClub 조회 및 운영진 여부 검증
        MemberClub memberClub = memberClubRepository.findById(memberClubId)
                .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

        if (!memberClub.is_Staff()) {
            throw new FeeException(FeeErrorCode.ACCESS_DENIED);
        }

        // 2. FeeNotice 조회
        FeeNotice feeNotice = feeNoticeRepository.findById(feeId)
                .orElseThrow(() -> new FeeException(FeeErrorCode.FEE_NOTICE_NOT_FOUND));

        // 3. FeeNotice 삭제
        feeStatusRepository.deleteAllByFeeNoticeId(feeId);
        feeNoticeRepository.delete(feeNotice);

        log.info("회비 공지가 삭제되었습니다. feeId: {}, memberClubId: {}", feeId, memberClubId);
    }
}
