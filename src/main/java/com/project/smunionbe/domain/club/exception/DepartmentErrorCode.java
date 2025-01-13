package com.project.smunionbe.domain.club.exception;

import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DepartmentErrorCode implements BaseErrorCode {
    // 부서 관련 에러
    DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Department404_0", "해당 부서를 찾을 수 없습니다."),
    DEPARTMENT_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Department404_1", "부서의 이름이 중복됩니다."),


    INVITECODE_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "Department400_1", "해당 부서의 승인 코드가 이미 존재합니다."),

    // 공통 처리
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Department400_0", "잘못된 요청입니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
