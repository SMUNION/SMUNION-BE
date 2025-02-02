package com.project.smunionbe.domain.notification.basic.exception;

import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BasicNoticeErrorCode implements BaseErrorCode {

    // 멤버 관련 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Basic404_1", "멤버 정보를 찾을 수 없습니다."),

    // 동아리 관련 에러
    CLUB_NOT_FOUND(HttpStatus.NOT_FOUND, "Basic404_2", "동아리 정보를 찾을 수 없습니다."),
    ACCESS_DENIED_CREATE(HttpStatus.FORBIDDEN, "Basic403_1", "일반 공지를 생성할 권한이 없습니다."),

    // 일반 공지 관련 에러
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Basic403_2","공지에 대한 권한이 없습니다."),
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "Basic404_3","공지 정보를 찾을 수 없습니다."),
    STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "Basic404_3","공지 상태 정보를 찾을 수 없습니다."),
    NO_TARGET_MEMBERS(HttpStatus.BAD_REQUEST, "Basic401_1","공지 대상 멤버가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
