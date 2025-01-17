package com.project.smunionbe.domain.notification.fee.exception;

import com.google.firebase.ErrorCode;
import com.project.smunionbe.domain.notification.vote.exception.VoteErrorCode;
import com.project.smunionbe.global.apiPayload.exception.CustomException;
import lombok.Getter;

@Getter
public class FeeException extends CustomException {

    public FeeException(FeeErrorCode errorCode){
        super(errorCode);
    }
}
