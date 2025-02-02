package com.project.smunionbe.domain.notification.basic.exception;

import com.project.smunionbe.global.apiPayload.exception.CustomException;
import lombok.Getter;

@Getter
public class BasicNoticeException extends CustomException {

    public BasicNoticeException(BasicNoticeErrorCode errorCode){
        super(errorCode);
    }
}
