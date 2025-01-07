package com.project.smunionbe.domain.club.exception;

import com.project.smunionbe.global.apiPayload.exception.CustomException;
import lombok.Getter;

@Getter
public class ClubException extends CustomException {
    public ClubException(ClubErrorCode errorCode) {
        super(errorCode);
    }
}
