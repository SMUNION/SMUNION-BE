package com.project.smunionbe.domain.member.service;

import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.exception.AuthErrorCode;
import com.project.smunionbe.domain.member.exception.AuthException;
import com.project.smunionbe.global.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;


    // 회원을 기반으로 새로운 액세스 토큰 생성
    public String createNewAccessTokenForMember(Member member) {
        // 액세스 토큰 생성 (유효시간 2시간 설정)
        return tokenProvider.generateAccessToken(member, Duration.ofHours(2));
        //return tokenProvider.generateAccessToken(member, Duration.ofSeconds(60));
    }

    // 회원을 기반으로 새로운 리프레시 토큰 생성
    public String createNewRefreshTokenForMember(Member member) {
        if (refreshTokenService.existsByMemberId(member.getId())) {
            refreshTokenService.deleteByMemberId(member.getId());
        }
        String refreshToken = tokenProvider.generateRefreshToken(member, Duration.ofDays(7)); // 리프레시 토큰 유효시간: 7일
        refreshTokenService.saveRefreshToken(member, refreshToken); // 리프레시 토큰 저장
        return refreshToken;
    }

    // 리프레시 토큰을 기반으로 새로운 액세스 토큰 생성
    public Map<String, String> createNewAccessToken(String refreshToken) {
        //토큰 유효성 검사에 실패하면 예외 발생
        if (!tokenProvider.validToken(refreshToken, "refresh")) {
            throw new AuthException(AuthErrorCode.REFRESH_TOKEN_INVALID);
        }

        // 리프레시 토큰을 기반으로 사용자 정보 조회
        Long memberId = refreshTokenService.findByRefreshToken(refreshToken).getMemberId();
        Member member = memberService.findById(memberId);

        // 리프레시 토큰 삭제 후 재발급 & 저장
        String newRefreshToken = "";
        if (refreshTokenService.existsByMemberId(memberId)) {
            refreshTokenService.deleteByMemberId(memberId);
            newRefreshToken = tokenProvider.generateRefreshToken(member, Duration.ofDays(7)); // 리프레시 토큰 유효시간: 7일
            refreshTokenService.saveRefreshToken(member, newRefreshToken); // 리프레시 토큰 저장
        } else {
            newRefreshToken = tokenProvider.generateRefreshToken(member, Duration.ofDays(7)); // 리프레시 토큰 유효시간: 7일
            refreshTokenService.saveRefreshToken(member, newRefreshToken); // 리프레시 토큰 저장
        }

        String newAccessToken = tokenProvider.generateAccessToken(member, Duration.ofHours(2));

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", newAccessToken);
        tokenMap.put("refreshToken", newRefreshToken);

        return tokenMap;
    }

    // 로그아웃 처리
    public void logout(String accessToken) {
        // Access Token 유효성 검사
        if (!tokenProvider.validToken(accessToken, "access")) {
            throw new AuthException(AuthErrorCode.JWT_TOKEN_INVALID);
        }

        // Access Token을 기반으로 회원 정보 조회
        Long memberId = tokenProvider.getUserId(accessToken); // 토큰에서 memberId 추출
        if (memberId == null) {
            throw new AuthException(AuthErrorCode.MEMBER_NOT_FOUND);
        }

        // 로그아웃 처리(리프레시 토큰 삭제)
        if (refreshTokenService.existsByMemberId(memberId)) {
            refreshTokenService.deleteByMemberId(memberId);
        } else { //로그인 되어있지 않을 때
            throw new AuthException(AuthErrorCode.NOT_LOGGED_IN);
        }
    }
}
