package com.project.smunionbe.domain.club.exception;


import com.google.firebase.ErrorCode;
import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ClubErrorCode implements BaseErrorCode {

    // 동아리 관련 에러
    CLUB_NOT_FOUND(HttpStatus.NOT_FOUND, "Club404_0", "해당 동아리를 찾을 수 없습니다."),
    CLUB_NAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Club404_1", "동아리의 이름이 중복됩니다."),

    // 권한 관련 에러
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Club403_0", "해당 기능을 사용할 수 없습니다."),


    // 공통 처리
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Club400_0", "잘못된 요청입니다."),

    // 레디스 관련 에러
    KEY_NOT_FOUND(HttpStatus.BAD_REQUEST, "Club400_1", "인증코드를 재생성해주십시오."),
    CODE_NOT_EQUAL(HttpStatus.BAD_REQUEST, "Club400_2", "인증코드가 일치하지 않습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
