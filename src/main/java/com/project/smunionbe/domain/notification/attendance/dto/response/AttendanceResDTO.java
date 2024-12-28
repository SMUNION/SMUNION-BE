package com.project.smunionbe.domain.notification.attendance.dto.response;

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
}
