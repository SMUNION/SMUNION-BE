package com.project.smunionbe.domain.member.exception;

import com.project.smunionbe.global.apiPayload.exception.CustomException;

public class AuthException extends CustomException {
    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}
