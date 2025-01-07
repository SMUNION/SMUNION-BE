package com.project.smunionbe.domain.member.converter;

import com.project.smunionbe.domain.member.dto.request.MemberRequestDTO;
import com.project.smunionbe.domain.member.entity.Member;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MemberConverter {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberConverter(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Member toMember(MemberRequestDTO.CreateMemberDTO dto) {
        return Member.builder()
                .email(dto.email())
                .password(bCryptPasswordEncoder.encode(dto.password()))
                .major(dto.major())
                .name(dto.name())
                .build();
    }
}
