package com.project.smunionbe.domain.notification.attendance.controller;

import com.project.smunionbe.domain.notification.attendance.dto.request.AttendanceReqDTO;
import com.project.smunionbe.domain.notification.attendance.service.command.AttendanceCommandService;
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

    @PostMapping("/{userId}")
    public ResponseEntity<CustomResponse<String>> createAttendance(
            @RequestBody @Valid AttendanceReqDTO.CreateAttendanceDTO request, @PathVariable Long userId) {
        attendanceCommandService.createAttendance(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.onSuccess(HttpStatus.CREATED,"출석 공지가 성공적으로 생성되었습니다."));
    }
}
