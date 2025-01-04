package com.project.smunionbe.domain.email.service;

import com.project.smunionbe.domain.member.repository.MemberRepository;
import com.project.smunionbe.global.util.RedisUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailAuthService {
    private final RedisUtil redisUtil;
    private final DefaultEmailSender emailSender;
    private final MemberRepository memberRepository;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis; //인증 코드의 유효시간

    private static final String AUTH_CODE_KEY_SUFFIX = ":code";

    // 회원가입 시 이메일 인증 코드를 생성하고 전송하는 메서드
    public void sendSignUpEmailAuthCode(String email) {
        // 이메일이 이미 DB에 존재하는지 확인
        if (memberRepository.existsByEmail(email)) {
            throw new RuntimeException("해당 이메일이 이미 존재합니다.");
        }

        String authCode = createCode(); // 인증 코드 생성
        emailSender.sendAuthCodeForSignUp(email, authCode); // 이메일로 인증 코드 전송
        redisUtil.save(email + ":code", authCode, authCodeExpirationMillis, TimeUnit.MILLISECONDS); // Redis에 인증 코드 저장
    }

    // 이메일 인증 코드를 검증하는 메서드
    public void verifyEmailAuthCode(String email, String authCode) {
        String storedAuthCode = (String) redisUtil.get(email + AUTH_CODE_KEY_SUFFIX);

        if (!authCode.equals(storedAuthCode)) {
            throw new RuntimeException("유효하지 않은 인증 코드입니다.");
        }
        redisUtil.delete(email + AUTH_CODE_KEY_SUFFIX); // 인증 성공 시 Redis에서 인증 코드 삭제
    }

    // 인증 코드 생성 메서드
    private String createCode() {
        int length = 8; // 인증 코드 길이
        StringBuilder builder = new StringBuilder();
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789"; // 소문자와 숫자 포함

        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * characters.length()); // 무작위 인덱스 생성
            builder.append(characters.charAt(randomIndex)); // 해당 인덱스의 문자를 추가
        }

        return builder.toString(); // 생성된 인증 코드 반환
    }
}
