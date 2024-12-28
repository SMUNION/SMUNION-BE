package com.project.smunionbe.domain.notification.attendance.exception;

import com.project.smunionbe.global.apiPayload.exception.CustomException;
import lombok.Getter;

@Getter
public class AttendanceException extends CustomException {

    public AttendanceException(AttendanceErrorCode errorCode){
        super(errorCode);
    }
}
