package com.project.smunionbe.domain.notification.attendance.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public class AttendanceReqDTO {

    public record CreateAttendanceDTO(
            Long clubId,
            String title,
            String content,
            List<String> targetDepartments, // 특정 부서들
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

    public record VerifyAttendanceRequest(
            Long attendanceId,
            Long clubId
    ){
    }
}
