package com.project.smunionbe.domain.member.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequestDTO {

    public record CreateMemberDTO(
            String email,
            String password,
            String major,
            String name
    ) {
    }
}
