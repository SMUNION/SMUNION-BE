package com.project.smunionbe.domain.notification.fee.exception;

import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FeeErrorCode implements BaseErrorCode {

    // 멤버 관련 에러
    MEMBER_CLUB_NOT_FOUND(HttpStatus.NOT_FOUND,"MEMBER_CLUB_NOT_FOUND", "해당 MemberClub 정보를 찾을 수 없습니다."),

    // 회비 관련 에러
    FEE_NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND,"FEE_NOTICE_NOT_FOUND", "해당 회비 공지를 찾을 수 없습니다."),

    // 권한 관련 에러
    ACCESS_DENIED(HttpStatus.FORBIDDEN,"ACCESS_DENIED", "해당 공지에 접근 권한이 없습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
