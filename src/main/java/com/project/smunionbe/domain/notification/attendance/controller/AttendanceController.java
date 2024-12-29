package com.project.smunionbe.domain.notification.attendance.controller;

import com.project.smunionbe.domain.notification.attendance.dto.request.AttendanceReqDTO;
import com.project.smunionbe.domain.notification.attendance.dto.response.AttendanceResDTO;
import com.project.smunionbe.domain.notification.attendance.service.command.AttendanceCommandService;
import com.project.smunionbe.domain.notification.attendance.service.query.AttendanceQueryService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices/attendance")
public class AttendanceController {

    private final AttendanceCommandService attendanceCommandService;
    private final AttendanceQueryService attendanceQueryService;

    @PostMapping("/{memberId}")
    public ResponseEntity<CustomResponse<String>> createAttendance(
            @RequestBody @Valid AttendanceReqDTO.CreateAttendanceDTO request, @PathVariable Long memberId) {
        attendanceCommandService.createAttendance(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.onSuccess(HttpStatus.CREATED,"출석 공지가 성공적으로 생성되었습니다."));
    }

    @GetMapping("/status/{memberId}")
    public CustomResponse<AttendanceResDTO.AttendanceAbsenteesResponse> getAbsentees(
            @RequestParam("id") Long attendanceId, @PathVariable Long memberId
    ) {
        AttendanceResDTO.AttendanceAbsenteesResponse response = attendanceQueryService.getAbsentees(attendanceId, memberId);
        return CustomResponse.onSuccess(response);
    }

    @GetMapping("/{memberId}")
    public CustomResponse<AttendanceResDTO.AttendanceListResponse> getAttendances(
            @RequestParam("id") Long clubId,                // 동아리 ID
            @RequestParam(value = "cursor", required = false) Long cursor, // 마지막 데이터 ID (Optional)
            @RequestParam(value = "offset", defaultValue = "10") int offset, // 몇 개의 데이터를 가져올지 (Default: 10)
            @PathVariable Long memberId
    ) {
        AttendanceResDTO.AttendanceListResponse response =
                attendanceQueryService.getAttendances(clubId, cursor, offset, memberId);
        return CustomResponse.onSuccess(response);
    }

    @GetMapping("/{id}/{memberId}")
    public CustomResponse<AttendanceResDTO.AttendanceDetailResponse> getAttendanceDetail(
            @PathVariable("id") Long attendanceId,
            @PathVariable Long memberId
    ) {
        AttendanceResDTO.AttendanceDetailResponse response = attendanceQueryService.getAttendanceDetail(attendanceId, memberId);
        return CustomResponse.onSuccess(response);
    }

    @PatchMapping("/{id}/{memberId}")
    public CustomResponse<String> updateAttendance(
            @PathVariable("id") Long attendanceId,
            @RequestBody @Valid AttendanceReqDTO.UpdateAttendanceRequest request,
            @PathVariable Long memberId
    ) {
        attendanceCommandService.updateAttendance(attendanceId, request, memberId);
        return CustomResponse.onSuccess("출석 공지 수정 성공");
    }
}
