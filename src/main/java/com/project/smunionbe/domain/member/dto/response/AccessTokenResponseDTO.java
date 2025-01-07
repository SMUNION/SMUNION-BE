package com.project.smunionbe.domain.member.dto.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccessTokenResponseDTO {
    public record CreateAccessTokenDTO(
            String accessToken
    ) {
    }
}
