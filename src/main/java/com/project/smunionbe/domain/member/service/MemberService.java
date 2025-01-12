package com.project.smunionbe.domain.member.service;

import com.project.smunionbe.domain.member.converter.MemberConverter;
import com.project.smunionbe.domain.member.dto.request.MemberRequestDTO;
import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.exception.AuthErrorCode;
import com.project.smunionbe.domain.member.exception.AuthException;
import com.project.smunionbe.domain.member.exception.MemberErrorCode;
import com.project.smunionbe.domain.member.exception.MemberException;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberConverter memberConverter;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Long save(MemberRequestDTO.CreateMemberDTO dto) {
        // 1. 입력값 검증
        // 이메일 검사
        if (dto.email() == null || dto.email().isEmpty()) {
            throw new MemberException(MemberErrorCode.INVALID_MEMBER_EMAIL);
        }
        if (!dto.email().matches("^[A-Za-z0-9+_.-]+@sangmyung.kr")) { //상명대 이메일로만 가입 가능
            throw new MemberException(MemberErrorCode.INVALID_EMAIL_FORMAT);
        }

        // 비밀번호 검사
        if (dto.password() == null || dto.password().isEmpty()) {
            throw new MemberException(MemberErrorCode.INVALID_MEMBER_PASSWORD);
        }
        if (dto.password().length() < 8 || !dto.password().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]+$")) {
            throw new MemberException(MemberErrorCode.INVALID_PASSWORD_FORMAT);
        }

        // 이름 검사
        if (dto.name() == null || dto.name().isEmpty()) {
            throw new MemberException(MemberErrorCode.INVALID_MEMBER_NAME);
        }

        // 전공 검사
        if (dto.major() == null || dto.major().isEmpty()) {
            throw new MemberException(MemberErrorCode.INVALID_MEMBER_MAJOR);
        }

        // 2. 중복 이메일 체크
        if (memberRepository.existsByEmail(dto.email())) {
            throw new MemberException(MemberErrorCode.DUPLICATE_MEMBER_EMAIL);
        }

        // 3. DTO를 엔티티로 변환
        Member member = memberConverter.toMember(dto);

        // 4. 멤버 저장
        try {
            return memberRepository.save(member).getId();
        } catch (Exception e) {
            // 데이터 저장 실패 시 예외 처리
            throw new MemberException(MemberErrorCode.MEMBER_REGISTRATION_FAILED);
        }
    }

    @Transactional
    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));
    }


    // 회원 인증 메서드
    @Transactional
    public Member authenticate(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(AuthErrorCode.MEMBER_NOT_FOUND));

        // 비밀번호 확인 (입력한 비밀번호와 DB에 저장된 비밀번호 비교)
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_MEMBER_PASSWORD);
        }

        return member;
    }



}
