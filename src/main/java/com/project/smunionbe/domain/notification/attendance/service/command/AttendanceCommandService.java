package com.project.smunionbe.domain.notification.attendance.service.command;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import com.project.smunionbe.domain.notification.attendance.converter.AttendanceConverter;
import com.project.smunionbe.domain.notification.attendance.dto.request.AttendanceReqDTO;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceStatus;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceErrorCode;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceException;
import com.project.smunionbe.domain.notification.attendance.repository.AttendanceRepository;
import com.project.smunionbe.domain.notification.attendance.repository.AttendanceStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AttendanceCommandService {

    private final AttendanceRepository attendanceRepository;
    private final ClubRepository clubRepository;
    private final MemberRepository memberRepository;
    private final MemberClubRepository memberClubRepository;
    private final AttendanceStatusRepository attendanceStatusRepository;

    public void createAttendance(AttendanceReqDTO.CreateAttendanceDTO reqDTO, Long memberId) {
        // 동아리 ID와 멤버 ID를 기반으로 해당 멤버가 동아리에 속해 있는지 확인
        boolean isMemberOfClub = memberClubRepository.existsByMemberIdAndClubId(memberId, reqDTO.clubId());
        if (!isMemberOfClub) {
            throw new AttendanceException(AttendanceErrorCode.ACCESS_DENIED);
        }

        Club club = clubRepository.findByIdAndMemberId(reqDTO.clubId(), memberId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ACCESS_DENIED));

        AttendanceNotice attendanceNotice = AttendanceConverter.toAttendanceNotice(reqDTO, club);

        attendanceRepository.save(attendanceNotice);
    }

    public void updateAttendance(Long attendanceId, AttendanceReqDTO.UpdateAttendanceRequest request, Long memberId) {
        AttendanceNotice attendanceNotice = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));

        if (!memberClubRepository.existsByMemberIdAndClubId(memberId, attendanceNotice.getClub().getId())) {
            throw new AttendanceException(AttendanceErrorCode.ACCESS_DENIED);
        }

        attendanceNotice.update(request.title(), request.content(), request.target(), request.date());
    }

    public void deleteAttendance(Long attendanceId, Long memberId) {
        AttendanceNotice attendanceNotice = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));

        if (!memberClubRepository.existsByMemberIdAndClubId(memberId, attendanceNotice.getClub().getId())) {
            throw new AttendanceException(AttendanceErrorCode.ACCESS_DENIED);
        }

        attendanceRepository.delete(attendanceNotice);
    }

    public void verifyAttendance(AttendanceReqDTO.VerifyAttendanceRequest request, Long memberClubId) {
        // 1. 출석 공지 조회
        AttendanceNotice attendanceNotice = attendanceRepository.findById(request.attendanceId())
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));

        // 2. AttendanceStatus 조회 및 업데이트
        AttendanceStatus status = attendanceStatusRepository.findByAttendanceAndMemberClub(attendanceNotice.getId(), memberClubId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ATTENDANCE_STATUS_NOT_FOUND));

        // 3. 출석 상태 업데이트
        if (status.getIsPresent()) {
            throw new AttendanceException(AttendanceErrorCode.ALREADY_PRESENT);
        }
        status.markPresent();
    }
}
