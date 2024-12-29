package com.project.smunionbe.domain.notification.attendance.service.query;

import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import com.project.smunionbe.domain.notification.attendance.converter.AttendanceConverter;
import com.project.smunionbe.domain.notification.attendance.dto.response.AttendanceResDTO;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceErrorCode;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceException;
import com.project.smunionbe.domain.notification.attendance.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceQueryService {

    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;

    public AttendanceResDTO.AttendanceAbsenteesResponse getAbsentees(Long attendanceId, Long memberId) {
        // 1. 출석 공지 조회 (존재하지 않을 경우 예외 발생)
        AttendanceNotice attendanceNotice = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));
        // 2. 동아리 권한 검증
        Long clubId = attendanceNotice.getClub().getId();
        if (!memberRepository.existsByIdAndClubId(memberId, clubId)) {
            throw new AttendanceException(AttendanceErrorCode.ACCESS_DENIED);
        }
        // 3. 미출석 인원 조회
        List<Member> absentees = memberRepository.findAbsenteesByAttendanceId(attendanceId, clubId);
        List<AttendanceResDTO.AttendanceAbsenteeDTO> absenteeDTOs = absentees.stream()
                .map(member -> new AttendanceResDTO.AttendanceAbsenteeDTO(member.getId(), member.getName()))
                .toList();
        return new AttendanceResDTO.AttendanceAbsenteesResponse(attendanceId, absenteeDTOs);
    }

    public AttendanceResDTO.AttendanceListResponse getAttendances(
            Long clubId, Long cursor, int offset, Long memberId
    ) {
        // 1. 동아리 권한 검증
        if (!memberRepository.existsByIdAndClubId(memberId, clubId)) {
            throw new AttendanceException(AttendanceErrorCode.ACCESS_DENIED);
        }
        // 2. 출석 공지 목록 조회 (페이징 처리)
        List<AttendanceNotice> attendanceNotices = attendanceRepository.findAttendancesWithPagination(clubId, cursor, offset);
        // 3. 데이터 변환
        List<AttendanceResDTO.AttendanceResponse> attendanceDTOs = attendanceNotices.stream()
                .map(notice -> new AttendanceResDTO.AttendanceResponse(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getContent(),
                        notice.getDate(),
                        notice.getCreatedAt()
                ))
                .toList();
        // 4. hasNext 계산 (조회된 데이터가 offset보다 큰지 확인)
        boolean hasNext = attendanceNotices.size() > offset;
        // 5. 다음 페이지의 cursor 값 설정
        Long nextCursor = hasNext ? attendanceNotices.get(attendanceNotices.size() - 1).getId() : null;
        return new AttendanceResDTO.AttendanceListResponse(attendanceDTOs, hasNext, nextCursor);
    }

    public AttendanceResDTO.AttendanceDetailResponse getAttendanceDetail(Long attendanceId, Long memberId) {
        // 1. 출석 공지 조회 (존재하지 않을 경우 예외 발생)
        AttendanceNotice attendanceNotice = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));
        // 2. 동아리 권한 검증
        if (!memberRepository.existsByIdAndClubId(memberId, attendanceNotice.getClub().getId())) {
            throw new AttendanceException(AttendanceErrorCode.ACCESS_DENIED);
        }
        return AttendanceConverter.toDetailResponse(attendanceNotice);
    }
}
