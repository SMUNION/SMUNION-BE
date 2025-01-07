package com.project.smunionbe.domain.club.exception;

import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DepartmentErrorCode implements BaseErrorCode {
    // 갤러리 관련 에러
    DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Department404_0", "해당 부서를 찾을 수 없습니다."),

    // 공통 처리
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Department400_0", "잘못된 요청입니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
