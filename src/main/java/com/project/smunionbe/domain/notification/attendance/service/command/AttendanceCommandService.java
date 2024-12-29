package com.project.smunionbe.domain.notification.attendance.service.command;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import com.project.smunionbe.domain.notification.attendance.converter.AttendanceConverter;
import com.project.smunionbe.domain.notification.attendance.dto.request.AttendanceReqDTO;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceErrorCode;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceException;
import com.project.smunionbe.domain.notification.attendance.repository.AttendanceRepository;
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

    public void createAttendance(AttendanceReqDTO.CreateAttendanceDTO reqDTO, Long memberId) {
        Club club = clubRepository.findByIdAndUserId(reqDTO.clubId(), memberId)
                .orElseThrow(() -> new RuntimeException("해당 동아리에 접근할 수 없습니다."));

        AttendanceNotice attendanceNotice = AttendanceConverter.toAttendanceNotice(reqDTO, club);

        attendanceRepository.save(attendanceNotice);
    }

    public void updateAttendance(Long attendanceId, AttendanceReqDTO.UpdateAttendanceRequest request, Long memberId) {
        AttendanceNotice attendanceNotice = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));

        if (!memberRepository.existsByIdAndClubId(memberId, attendanceNotice.getClub().getId())) {
            throw new AccessDeniedException("해당 동아리에 접근할 수 없습니다.");
        }

        attendanceNotice.update(request.title(), request.content(), request.target(), request.date());
    }
}
