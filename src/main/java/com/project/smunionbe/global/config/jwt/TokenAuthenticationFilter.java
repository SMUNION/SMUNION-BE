package com.project.smunionbe.global.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.smunionbe.domain.member.exception.AuthErrorCode;
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
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        return requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/swagger-ui.html") ||
                requestURI.startsWith("/api/v1/users/login") ||  //  로그인 API 필터링 제외
                requestURI.startsWith("/api/v1/users/signup") ||   //  회원가입 API 필터링 제외
                requestURI.startsWith("/api/v1/users/refresh");   //  Access Token 재발급 API 필터링 제외
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String token = getAccessToken(authorizationHeader);

        if (token != null && tokenProvider.validToken(token, "access")) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            handleInvalidToken(response, token);
            return; // 필터 체인 중단
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    private void handleInvalidToken(HttpServletResponse response, String token) throws IOException {
        CustomResponse<Object> customResponse;
        if (token == null || tokenProvider.isTokenExpired(token)) {
            customResponse = CustomResponse.onFailure(
                    String.valueOf(AuthErrorCode.JWT_TOKEN_EXPIRED.getStatus().value()),
                    AuthErrorCode.JWT_TOKEN_EXPIRED.getMessage()
            );
            response.setStatus(AuthErrorCode.JWT_TOKEN_EXPIRED.getStatus().value());
        } else {
            customResponse = CustomResponse.onFailure(
                    String.valueOf(AuthErrorCode.JWT_TOKEN_INVALID.getStatus().value()),
                    AuthErrorCode.JWT_TOKEN_INVALID.getMessage()
            );
            response.setStatus(AuthErrorCode.JWT_TOKEN_INVALID.getStatus().value());
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(customResponse));
    }
}
