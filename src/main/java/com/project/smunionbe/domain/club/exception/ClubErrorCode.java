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

    // 권한 관련 에러
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Club403_0", "해당 동아리에 접근할 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Member404_0", "해당 멤버를 찾을 수 없습니다."),

    // 공통 처리
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Club400_0", "잘못된 요청입니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
