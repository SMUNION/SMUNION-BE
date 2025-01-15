package com.project.smunionbe.domain.notification.attendance.service.query;

import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import com.project.smunionbe.domain.notification.attendance.converter.AttendanceConverter;
import com.project.smunionbe.domain.notification.attendance.dto.response.AttendanceResDTO;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceErrorCode;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceException;
import com.project.smunionbe.domain.notification.attendance.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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
    private final MemberClubRepository memberClubRepository;

    public AttendanceResDTO.AttendanceAbsenteesResponse getAbsentees(Long attendanceId, Long selectedMemberClubId) {
        // 1. MemberClub 조회
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.MEMBER_NOT_FOUND));

        // 2. AttendanceNotice 조회
        AttendanceNotice attendanceNotice = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));

        // 3. Club 권한 확인
        Long clubId = memberClub.getClub().getId();
        if (!memberClub.getClub().equals(attendanceNotice.getClub())) {
            throw new AttendanceException(AttendanceErrorCode.ACCESS_DENIED);
        }

        // 4. "전체" 여부 확인
        boolean isAll = "전체".equals(attendanceNotice.getTarget());

        // 5. 미출석 인원 조회
        List<Member> absentees = memberRepository.findAbsenteesByAttendanceId(attendanceId, clubId, isAll);

        // 6. DTO 변환
        List<AttendanceResDTO.AttendanceAbsenteeDTO> absenteeDTOs = absentees.stream()
                .map(member -> new AttendanceResDTO.AttendanceAbsenteeDTO(member.getId(), member.getName()))
                .toList();

        return new AttendanceResDTO.AttendanceAbsenteesResponse(attendanceId, absenteeDTOs);
    }


    public AttendanceResDTO.AttendanceListResponse getAttendances(Long cursor, int size, Long selectedMemberClubId) {
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.MEMBER_NOT_FOUND));

        Slice<AttendanceNotice> attendanceSlice = attendanceRepository.findByClubIdAndCursor(
                memberClub.getClub().getId(),
                cursor,
                PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"))
        );

        Long nextCursor = attendanceSlice.hasNext()
                ? attendanceSlice.getContent().get(attendanceSlice.getContent().size() - 1).getId()
                : null;

        List<AttendanceResDTO.AttendanceResponse> attendanceDTOs = attendanceSlice.getContent()
                .stream()
                .map(notice -> new AttendanceResDTO.AttendanceResponse(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getContent(),
                        notice.getDate(),
                        notice.getCreatedAt()
                ))
                .toList();

        return new AttendanceResDTO.AttendanceListResponse(attendanceDTOs, attendanceSlice.hasNext(), nextCursor);
    }

    public AttendanceResDTO.AttendanceDetailResponse getAttendanceDetail(Long attendanceId, Long selectedMemberClubId) {
        MemberClub memberClub = memberClubRepository.findById(selectedMemberClubId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.MEMBER_NOT_FOUND));

        AttendanceNotice attendanceNotice = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AttendanceException(AttendanceErrorCode.ATTENDANCE_NOT_FOUND));

        if (!memberClub.getClub().equals(attendanceNotice.getClub())) {
            throw new AttendanceException(AttendanceErrorCode.ACCESS_DENIED);
        }

        return AttendanceConverter.toDetailResponse(attendanceNotice);
    }
}

