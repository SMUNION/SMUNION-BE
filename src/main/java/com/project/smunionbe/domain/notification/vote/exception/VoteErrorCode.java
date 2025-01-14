package com.project.smunionbe.domain.notification.vote.exception;

import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum VoteErrorCode implements BaseErrorCode {

    // 멤버 관련 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Vote404_1", "멤버 정보를 찾을 수 없습니다."),

    // 동아리 관련 에러
    CLUB_NOT_FOUND(HttpStatus.NOT_FOUND, "Vote404_2", "동아리 정보를 찾을 수 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Vote403_1", "투표 공지를 생성할 권한이 없습니다."),

    // 투표 공지 관련 에러
    VOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "Vote404_3", "투표 공지를 찾을 수 없습니다."),
    INVALID_VOTE_ITEM(HttpStatus.BAD_REQUEST, "Vote400_1", "잘못된 투표 항목입니다."),
    DUPLICATE_VOTE_NOT_ALLOWED(HttpStatus.CONFLICT, "Vote409_1", "중복 투표는 허용되지 않습니다."),
    VOTING_CLOSED(HttpStatus.GONE, "Vote410_1", "투표가 마감되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
