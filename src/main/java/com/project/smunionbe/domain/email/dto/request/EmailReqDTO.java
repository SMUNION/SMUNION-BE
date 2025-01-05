package com.project.smunionbe.domain.email.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class EmailReqDTO {

    public record SendEmailRequestDTO(
            String email
    ){
    }

    public record VerifyEmailRequestDTO(
            String email,
            String code
    ){
    }
}
