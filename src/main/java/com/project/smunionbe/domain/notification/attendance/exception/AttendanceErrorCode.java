package com.project.smunionbe.domain.notification.attendance.exception;

import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AttendanceErrorCode implements BaseErrorCode {

    ATTENDANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Attendance404_0", "해당 출석공지를 찾을 수 없습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
