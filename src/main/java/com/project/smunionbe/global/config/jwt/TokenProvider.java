package com.project.smunionbe.global.config.jwt;

import com.project.smunionbe.domain.member.entity.Member;

import com.project.smunionbe.domain.member.exception.AuthErrorCode;
import com.project.smunionbe.domain.member.exception.AuthException;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import com.project.smunionbe.domain.member.security.CustomUserDetails;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtProperties jwtProperties;
    private final MemberRepository memberRepository;

    public String generateToken(Member member, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), member);
    }

    //JWT 토큰 생성 메서드
    private String makeToken(Date expiry, Member member) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(member.getEmail())
                .claim("id", member.getId())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }



    //JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()).build() //비밀값으로 복호화
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) { //복호화 과정에서 에러가 나면 유효하지 않은 토큰
            return false;
        }
    }

    //토큰 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Long memberId = claims.get("id", Long.class);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 사용자 ID"));

        // CustomUserDetails로 인증 정보 생성
        CustomUserDetails userDetails = new CustomUserDetails(member);
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }


    //토큰 기반으로 유저 ID를 가져오는 메서드
    public Long getUserId(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.get("id", Long.class);
        } catch (ExpiredJwtException e) {
            // 토큰 만료
            throw new AuthException(AuthErrorCode.JWT_TOKEN_EXPIRED);
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            // 잘못된 형식의 토큰
            throw new AuthException(AuthErrorCode.JWT_TOKEN_MALFORMED);
        } catch (SignatureException e) {
            // 서명이 유효하지 않은 경우
            throw new AuthException(AuthErrorCode.JWT_SIGNATURE_INVALID);
        } catch (Exception e) {
            // 그 외 일반적인 JWT 인증 실패
            throw new AuthException(AuthErrorCode.JWT_AUTHENTICATION_FAILED);
        }
    }

    //클레임을 조회해서 반환하는 메서드
    private Claims getClaims(String token) {
        return Jwts.parser() //클레임 조회
                .setSigningKey(jwtProperties.getSecretKey()).build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Request의 Header로부터 토큰 값 조회
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }

    // Request Header에 Refresh Token 정보를 추출하는 메서드
    public String resolveRefreshToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Refresh");
        if (StringUtils.hasText(bearerToken)) {
            return bearerToken;
        }
        return null;
    }





}
