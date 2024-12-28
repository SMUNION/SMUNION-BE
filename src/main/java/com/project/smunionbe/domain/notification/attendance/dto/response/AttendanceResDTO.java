package com.project.smunionbe.domain.notification.attendance.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AttendanceResDTO {

    public record AttendanceAbsenteeDTO(
            Long memberId,
            String nickname
    ){
    }

    public record AttendanceAbsenteesResponse(
            Long AttendanceId,
            List<AttendanceAbsenteeDTO> absentees
    ){
    }

    public record AttendanceResponse(
            Long attendanceId,
            String title,
            String content,
            LocalDateTime date,
            LocalDateTime createdAt
    ){
    }

    public record AttendanceListResponse(
            List<AttendanceResponse> attendances,
            boolean hasNext,
            Long cursor
    ){
    }
}
