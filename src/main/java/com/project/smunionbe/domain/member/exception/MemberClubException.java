package com.project.smunionbe.domain.member.exception;

import com.project.smunionbe.global.apiPayload.exception.CustomException;

public class MemberClubException extends CustomException {
    public MemberClubException(MemberClubErrorCode errorCode) {
        super(errorCode);
    }
}
