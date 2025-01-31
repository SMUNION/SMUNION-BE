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
    JWT_TOKEN_NULL(HttpStatus.UNAUTHORIZED, "Auth401_5", "JWT 토큰이 비어있습니다. 토큰과 함께 요청해주세요."),
    JWT_AUTHENTICATION_FAILED(HttpStatus.FORBIDDEN, "Auth403_0", "JWT 인증에 실패했습니다."),
    INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN, "Auth403_1", "권한이 부족합니다."),

    // 사용자 관련 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Auth404_0", "존재하지 않는 유저입니다."),
    MEMBER_ALREADY_DELETED(HttpStatus.NOT_FOUND, "Auth404_1", "이미 탈퇴된 회원입니다."),
    MEMBER_DELETED(HttpStatus.NOT_FOUND, "Auth404_2", "탈퇴한 계정입니다."),
    INVALID_MEMBER_PASSWORD(HttpStatus.BAD_REQUEST, "Auth400_0", "잘못된 비밀번호입니다."),

    // 로그인 관련 에러
    ALREADY_LOGGED_IN(HttpStatus.BAD_REQUEST, "Auth400_1", "이미 로그인된 상태입니다."),
    NOT_LOGGED_IN(HttpStatus.BAD_REQUEST, "Auth400_2", "로그인 상태가 아닙니다."),

    // 비밀번호 관련 에러
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Auth401_1", "현재 비밀번호가 올바르지 않습니다."),
    PASSWORDS_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, "Auth400_3", "새 비밀번호가 일치하지 않습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "Auth400_4", "비밀번호 형식이 올바르지 않습니다. 비밀번호는 최소 8자 이상이어야 하며, 알파벳과 숫자가 포함된 조합이어야 합니다.");




    private final HttpStatus status;
    private final String code;
    private final String message;
}