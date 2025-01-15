package com.project.smunionbe.domain.community.exception;

import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommunityErrorCode implements BaseErrorCode {
    // 사용자 관련 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Auth404_0", "존재하지 않는 유저입니다."),

    // 게시글 관련 에러
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Article404_0", "게시글이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}