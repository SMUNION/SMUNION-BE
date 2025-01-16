package com.project.smunionbe.global.config.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.smunionbe.domain.member.exception.AuthErrorCode;
import com.project.smunionbe.domain.member.exception.AuthException;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

        //가져온 값에서 접두사 제거
        String token = getAccessToken(authorizationHeader);

        //가져온 토큰이 유효한지 확인하고, 유효한 때에는 인증 정보 설정
        if (tokenProvider.validToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            // 토큰이 만료되었거나 유효하지 않은 경우 구분하여 응답 처리
            if (tokenProvider.isTokenExpired(token)) {  // 만료된 토큰 확인
                CustomResponse<Object> customResponse = CustomResponse.onFailure(
                        String.valueOf(AuthErrorCode.JWT_TOKEN_EXPIRED.getStatus().value()),  // 상태 코드
                        AuthErrorCode.JWT_TOKEN_EXPIRED.getMessage()  // 만료된 토큰 에러 메시지
                );

                response.setStatus(AuthErrorCode.JWT_TOKEN_EXPIRED.getStatus().value());
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write(new ObjectMapper().writeValueAsString(customResponse));
            } else {
                CustomResponse<Object> customResponse = CustomResponse.onFailure(
                        String.valueOf(AuthErrorCode.JWT_TOKEN_INVALID.getStatus().value()),  // 상태 코드
                        AuthErrorCode.JWT_TOKEN_INVALID.getMessage()  // 유효하지 않은 토큰 에러 메시지
                );

                response.setStatus(AuthErrorCode.JWT_TOKEN_INVALID.getStatus().value());
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write(new ObjectMapper().writeValueAsString(customResponse));
            }
            return; // 더 이상 필터 체인 진행하지 않도록 설정
        }

        filterChain.doFilter(request, response);
    }


    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
