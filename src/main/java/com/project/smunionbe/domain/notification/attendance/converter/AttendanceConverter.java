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

        return AttendanceNotice.builder()
                .club(club)
                .content(reqDTO.content())
                .title(reqDTO.title())
                .target(reqDTO.target())
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
