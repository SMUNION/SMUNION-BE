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

    public record ChangePasswordDTO(
            String currentPassword,  // 현재 비밀번호
            String newPassword,      // 새 비밀번호
            String confirmPassword   // 새 비밀번호 확인
    ) {}

    public record FindPasswordDTO(
            String email
    ) {}


}
