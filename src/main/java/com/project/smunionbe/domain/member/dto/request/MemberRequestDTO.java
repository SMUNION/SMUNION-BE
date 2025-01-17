package com.project.smunionbe.domain.member.dto.request;

public class MemberRequestDTO {

    public record CreateMemberDTO(
            String email,
            String password,
            String major,
            String name
    ) {
    }

    public record LoginMemberDTO(
            String email,
            String password
    ) {

    }

}
