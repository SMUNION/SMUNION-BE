package com.project.smunionbe.domain.club.exception;

import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GalleryErrorCode implements BaseErrorCode {

    // 갤러리 관련 에러
    GALLERY_NOT_FOUND(HttpStatus.NOT_FOUND, "Gallery404_0", "해당 갤러리를 찾을 수 없습니다."),

    // 공통 처리
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Club400_0", "잘못된 요청입니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
