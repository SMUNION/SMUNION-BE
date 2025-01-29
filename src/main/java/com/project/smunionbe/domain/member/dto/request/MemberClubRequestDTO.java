package com.project.smunionbe.domain.member.dto.request;

public class MemberClubRequestDTO {
    public record ChangeNicknameDTO(
            String newNickname
    ) {}
}
