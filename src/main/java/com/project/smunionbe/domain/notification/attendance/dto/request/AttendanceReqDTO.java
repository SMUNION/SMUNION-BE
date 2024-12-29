package com.project.smunionbe.domain.notification.attendance.dto.request;

import java.time.LocalDateTime;

public class AttendanceReqDTO {

    public record CreateAttendanceDTO(
            Long clubId,
            String title,
            String content,
            String target,
            LocalDateTime date
    ) {
    }

    public record UpdateAttendanceRequest(
            String title,
            String content,
            String target,
            LocalDateTime date
    ){
    }
}
