package com.project.smunionbe.domain.member.exception;

import com.project.smunionbe.global.apiPayload.code.BaseErrorCode;
import com.project.smunionbe.global.apiPayload.exception.CustomException;

public class MemberException extends CustomException {
    public MemberException(MemberErrorCode errorCode) {
        super(errorCode);
    }
}
