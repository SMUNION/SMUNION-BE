package com.project.smunionbe.domain.member.dto.request;


public class AccessTokenRequestDTO {
    public record CreateAccessTokenDTO(
            String refreshToken
    ) {
    }
}
