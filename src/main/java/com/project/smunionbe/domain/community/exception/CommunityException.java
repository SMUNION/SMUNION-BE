package com.project.smunionbe.domain.community.exception;

import com.project.smunionbe.global.apiPayload.exception.CustomException;

public class CommunityException extends CustomException {
    public CommunityException(CommunityErrorCode errorCode) {
        super(errorCode);
    }
}