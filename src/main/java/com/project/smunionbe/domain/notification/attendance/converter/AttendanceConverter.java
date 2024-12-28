package com.project.smunionbe.domain.notification.attendance.converter;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.notification.attendance.dto.request.AttendanceReqDTO;
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
}
