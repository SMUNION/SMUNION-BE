package com.project.smunionbe.domain.notification.attendance.exception;

import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AttendanceErrorCode implements BaseErrorCode {

    // 출석 공지 관련 에러
    ATTENDANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Attendance404_0", "해당 출석 공지를 찾을 수 없습니다."),
    INVALID_ATTENDANCE_DATE(HttpStatus.BAD_REQUEST, "Attendance400_1", "출석 날짜가 유효하지 않습니다."),

    // 출석 상태 관련 에러
    ATTENDANCE_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "AttendanceStatus404_0", "출석 상태를 찾을 수 없습니다."),
    ALREADY_PRESENT(HttpStatus.CONFLICT, "Attendance409_0", "이미 출석이 완료되었습니다."),
    INVALID_STATUS_UPDATE(HttpStatus.BAD_REQUEST, "Attendance400_2", "출석 상태를 업데이트할 수 없습니다."),

    // 권한 관련 에러
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Attendance403_0", "해당 동아리에 접근할 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Member404_0", "해당 멤버를 찾을 수 없습니다."),

    // 공통 처리
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Attendance400_0", "잘못된 요청입니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
