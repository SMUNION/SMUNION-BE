package com.project.smunionbe.domain.club.exception;

import com.project.smunionbe.global.apiPayload.exception.CustomException;
import lombok.Getter;

@Getter
public class DepartmentException extends CustomException {
    public DepartmentException(DepartmentErrorCode errorCode) {
        super(errorCode);
    }
}
