package com.project.smunionbe.domain.community.exception;

import com.project.smunionbe.global.apiPayload.exception.CustomException;

public class ArticleException extends CustomException {
    public ArticleException(ArticleErrorCode errorCode) {
        super(errorCode);
    }
}