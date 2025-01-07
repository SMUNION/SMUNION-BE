package com.project.smunionbe.domain.member.exception;

import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {
    // 인증 관련 에러
    JWT_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Auth401_0", "JWT 토큰이 만료되었습니다."),
    JWT_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Auth401_1", "JWT 토큰이 유효하지 않습니다."),
    JWT_TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, "Auth401_2", "JWT 토큰 형식이 올바르지 않습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Auth401_3", "refresh 토큰이 유효하지 않습니다."),
    JWT_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "Auth401_4", "JWT 서명이 유효하지 않습니다."),
    JWT_AUTHENTICATION_FAILED(HttpStatus.FORBIDDEN, "Auth403_0", "JWT 인증에 실패했습니다."),
    INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN, "Auth403_1", "권한이 부족합니다."),

    // 사용자 관련 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Auth404_0", "존재하지 않는 유저입니다."),
    INVALID_MEMBER_PASSWORD(HttpStatus.BAD_REQUEST, "Auth400_0", "잘못된 비밀번호입니다."),

    // 로그인 관련 에러
    ALREADY_LOGGED_IN(HttpStatus.BAD_REQUEST, "Auth400_1", "이미 로그인된 상태입니다."),
    NOT_LOGGED_IN(HttpStatus.BAD_REQUEST, "Auth400_2", "로그인 상태가 아닙니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}