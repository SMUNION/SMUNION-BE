package com.project.smunionbe.domain.notification.vote.exception;

import com.google.firebase.ErrorCode;
import com.project.smunionbe.domain.notification.attendance.exception.AttendanceErrorCode;
import com.project.smunionbe.global.apiPayload.exception.CustomException;
import lombok.Getter;

@Getter
public class VoteException extends CustomException {

    public VoteException(VoteErrorCode errorCode){
        super(errorCode);
    }
}
