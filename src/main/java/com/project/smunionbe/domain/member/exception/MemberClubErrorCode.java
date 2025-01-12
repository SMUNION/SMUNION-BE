package com.project.smunionbe.domain.member.exception;

import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberClubErrorCode implements BaseErrorCode {
    // 동아리 관련 에러
    MEMBER_CLUB_NOT_FOUND(HttpStatus.NOT_FOUND, "Auth404_1", "해당 사용자가 가입한 동아리가 없습니다."),
    CLUB_NOT_FOUND(HttpStatus.NOT_FOUND, "Auth404_2", "해당 동아리가 존재하지 않습니다."),
    DEPARTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Auth404_3", "해당 동아리 부서가 존재하지 않습니다."),

    // 공통 처리
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Member400_0", "잘못된 요청입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
