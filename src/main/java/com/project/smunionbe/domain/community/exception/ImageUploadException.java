package com.project.smunionbe.domain.community.exception;

import com.project.smunionbe.global.apiPayload.exception.CustomException;

public class ImageUploadException extends CustomException {
    public ImageUploadException(ImageUploadErrorCode errorCode) {
        super(errorCode);
    }
}