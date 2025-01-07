package com.project.smunionbe.domain.notification.fcm.exception;

import com.project.smunionbe.global.apiPayload.exception.CustomException;
import lombok.Getter;

@Getter
public class FCMException extends CustomException {

    public FCMException(FCMErrorCode errorCode){
        super(errorCode);
    }
}
