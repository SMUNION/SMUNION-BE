package com.project.smunionbe.domain.member.repository;

import com.project.smunionbe.domain.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMemberId(Long memberId);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    boolean existsByMemberId(Long memberId);

    void deleteByRefreshToken(String refreshToken); // Refresh token을 삭제
    void deleteByMemberId(Long memberId);
}
