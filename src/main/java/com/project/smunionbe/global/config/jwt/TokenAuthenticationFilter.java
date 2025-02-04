package com.project.smunionbe.global.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.exception.AuthErrorCode;
import com.project.smunionbe.domain.member.exception.MemberErrorCode;
import com.project.smunionbe.domain.member.exception.MemberException;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 인증 없이 허용하는 GET 요청
        boolean isAllowedGetRequest =
                HttpMethod.GET.matches(method) && (
                        requestURI.equals("/api/v1/community") ||
                                requestURI.matches("^/api/v1/community/\\d+$") || // /api/v1/community/{articleId}
                                requestURI.matches("^/api/v1/community/\\d+/replies$") // /api/v1/community/{articleId}/replies 추가
                );

        return isAllowedGetRequest || (
                requestURI.equals("") ||
                        requestURI.equals("/") ||
                        requestURI.equals("/favicon.ico") ||
                        requestURI.startsWith("/swagger-ui") ||
                        requestURI.startsWith("/v3/api-docs") ||
                        requestURI.startsWith("/swagger-resources") ||
                        requestURI.startsWith("/swagger-ui.html") ||
                        requestURI.startsWith("/api/v1/email/send/signup") ||
                        requestURI.startsWith("/api/v1/users/signup") ||
                        requestURI.startsWith("/api/v1/users/login") ||
                        requestURI.startsWith("/api/v1/users/refresh") ||  //  Access Token 재발급 API 필터링 제외
                        requestURI.startsWith("/api/v1/email/verify"));


    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        String token = getAccessToken(authorizationHeader);

        if (token != null && tokenProvider.validToken(token, "access")) {

            //탈퇴한 회원인지 여부 확인
            Claims claims = tokenProvider.getClaims(token);
            Long memberId = claims.get("id", Long.class);
            String deletedAt = claims.get("deletedAt") != null ? claims.get("deletedAt").toString() : null;

            CustomResponse<Object> customResponse;

            //탈퇴한 회원이면 요청 차단
            if (deletedAt != null) {
                customResponse = CustomResponse.onFailure(
                        String.valueOf(AuthErrorCode.MEMBER_DELETED.getStatus().value()),
                        AuthErrorCode.MEMBER_DELETED.getMessage()
                );
                response.setStatus(AuthErrorCode.MEMBER_DELETED.getStatus().value());
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write(new ObjectMapper().writeValueAsString(customResponse));
                return;
            }

            // JWT 내부 deleted_at이 없을 경우에만 DB 조회
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

            if (member.getDeletedAt() != null) {
                customResponse = CustomResponse.onFailure(
                        String.valueOf(AuthErrorCode.MEMBER_DELETED.getStatus().value()),
                        AuthErrorCode.MEMBER_DELETED.getMessage()
                );
                response.setStatus(AuthErrorCode.MEMBER_DELETED.getStatus().value());
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write(new ObjectMapper().writeValueAsString(customResponse));
                return;
            }

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
