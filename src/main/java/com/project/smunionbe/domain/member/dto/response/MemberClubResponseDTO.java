package com.project.smunionbe.domain.member.dto.response;

public class MemberClubResponseDTO {

    public record MemberClubResponse(
            Long memberClubId,
            String departmentName,
            String clubName,
            String nickname
    ) {}
}

