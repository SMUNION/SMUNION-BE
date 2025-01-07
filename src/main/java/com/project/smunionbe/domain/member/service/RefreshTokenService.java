package com.project.smunionbe.domain.member.service;

import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.entity.RefreshToken;
import com.project.smunionbe.domain.member.exception.AuthErrorCode;
import com.project.smunionbe.domain.member.exception.AuthException;
import com.project.smunionbe.domain.member.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveRefreshToken(Member member, String refreshToken) {
        RefreshToken token = new RefreshToken(member.getId(), refreshToken);
        refreshTokenRepository.save(token); // DB에 저장
    }

    @Transactional
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new AuthException(AuthErrorCode.JWT_TOKEN_INVALID));
    }

    @Transactional
    // 리프레시 토큰 여부 확인
    public boolean existsByMemberId(Long memberId) {
        return refreshTokenRepository.existsByMemberId(memberId);
    }

    // 리프레시 토큰 삭제
    @Transactional
    public void deleteByMemberId(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }
}
