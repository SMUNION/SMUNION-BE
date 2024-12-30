package com.project.smunionbe.domain.notification.attendance.converter;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.notification.attendance.dto.request.AttendanceReqDTO;
import com.project.smunionbe.domain.notification.attendance.dto.response.AttendanceResDTO;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttendanceConverter {

    public static AttendanceNotice toAttendanceNotice(AttendanceReqDTO.CreateAttendanceDTO reqDTO, Club club) {

        // 타겟 부서가 없으면 "전체"로 설정
        String target = (reqDTO.targetDepartments() == null || reqDTO.targetDepartments().isEmpty())
                ? "전체"
                : String.join(", ", reqDTO.targetDepartments()); // 부서 이름들을 콤마로 합치기

        return AttendanceNotice.builder()
                .club(club)
                .content(reqDTO.content())
                .title(reqDTO.title())
                .target(target) // 변경된 타겟 정보
                .date(reqDTO.date())
                .build();
    }

    // AttendanceNotice → AttendanceDetailResponse 변환
    public static AttendanceResDTO.AttendanceDetailResponse toDetailResponse(AttendanceNotice notice) {
        return new AttendanceResDTO.AttendanceDetailResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getTarget(),
                notice.getDate(),
                notice.getCreatedAt()
        );
    }
}
