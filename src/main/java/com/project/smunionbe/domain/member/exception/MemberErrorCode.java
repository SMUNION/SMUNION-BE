package com.project.smunionbe.domain.member.exception;

import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {
    // 멤버 관련 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "Member404_0", "해당 멤버를 찾을 수 없습니다."),
    DUPLICATE_MEMBER_EMAIL(HttpStatus.CONFLICT, "Member409_0", "이미 사용 중인 이메일입니다."),
    INVALID_MEMBER_PASSWORD(HttpStatus.BAD_REQUEST, "Member400_1", "잘못된 비밀번호입니다."),
    MEMBER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "Member403_0", "멤버 접근이 거부되었습니다."),
    MEMBER_REGISTRATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Member500_0", "멤버 등록 중 오류가 발생했습니다."),

    // 입력값 검증 에러
    INVALID_MEMBER_NAME(HttpStatus.BAD_REQUEST, "Member400_1", "이름이 유효하지 않습니다."),
    INVALID_MEMBER_EMAIL(HttpStatus.BAD_REQUEST, "Member400_2", "이메일 형식이 유효하지 않습니다."),
    INVALID_MEMBER_MAJOR(HttpStatus.BAD_REQUEST, "Member400_5", "전공 정보가 유효하지 않습니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "Member400_3", "이메일 형식이 올바르지 않습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "Member400_4", "비밀번호 형식이 올바르지 않습니다. 비밀번호는 최소 8자 이상이어야 하며, 알파벳과 숫자가 포함된 조합이어야 합니다."),


    // 멤버 상태 관련 에러
    MEMBER_ALREADY_ACTIVE(HttpStatus.CONFLICT, "Member409_1", "이미 활성화된 멤버입니다."),
    MEMBER_ALREADY_INACTIVE(HttpStatus.CONFLICT, "Member409_2", "이미 비활성화된 멤버입니다."),
    MEMBER_STATUS_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Member500_1", "멤버 상태 업데이트에 실패했습니다."),

    //프로필 이미지 관련 에러
    PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Member404_1", "프로필 사진이 존재하지 않습니다."),

    // 공통 처리
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Member400_0", "잘못된 요청입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
