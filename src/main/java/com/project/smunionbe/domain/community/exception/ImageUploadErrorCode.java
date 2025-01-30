package com.project.smunionbe.domain.community.exception;

import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ImageUploadErrorCode implements BaseErrorCode {
    // 클라이언트 요청 관련 에러
    NO_IMAGE_PROVIDED(HttpStatus.BAD_REQUEST, "Image400_0", "업로드할 이미지가 없습니다."),
    INVALID_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "Image400_1", "지원되지 않는 이미지 형식입니다."),

    // 서버 관련 에러
    UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Image500_0", "이미지 업로드 중 오류가 발생했습니다."),
    S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Image500_1", "AWS S3 업로드에 실패했습니다."),
    FILE_READ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Image500_2", "이미지 파일을 읽는 중 오류가 발생했습니다."),
    S3_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Image500_3", "AWS S3 이미지 삭제에 실패했습니다."),
    IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Image500_4", "DB 이미지 삭제에 실패했습니다."),


    // 권한 관련 에러
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "Image401_0", "이미지 업로드 권한이 없습니다."),

    // 기타
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Image500_99", "알 수 없는 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
