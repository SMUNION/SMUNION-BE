package com.project.smunionbe.domain.club.exception;

import com.project.smunionbe.global.apiPayload.exception.CustomException;
import lombok.Getter;

@Getter
public class GalleryException extends CustomException {
    public GalleryException(GalleryErrorCode errorCode) {
        super(errorCode);
    }
}

