package com.project.smunionbe.domain.member.dto.response;

public class MemberResponseDTO {
    public record MemberProfileResponse(
            Long id,
            String name,
            String major,
            String studentNumber
    ) {}
}
