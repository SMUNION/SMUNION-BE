package com.project.smunionbe.domain.member.controller;

import com.project.smunionbe.domain.member.converter.MemberClubConverter;
import com.project.smunionbe.domain.member.dto.response.MemberClubResponseDTO;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.domain.member.service.MemberClubService;
import com.project.smunionbe.domain.member.service.MemberService;
import com.project.smunionbe.domain.member.service.TokenService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import com.project.smunionbe.global.config.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "동아리 회원 API", description = "-")
public class MemberClubController {
    private final MemberService memberService;
    private final MemberClubService memberClubService;
    private final MemberClubConverter memberClubConverter;
    private final TokenProvider tokenProvider;

    // 해당 멤버가 가입되어있는 모든 동아리 조회
    @GetMapping("/clubs")
    @Operation(
            summary = "해당 멤버가 가입되어있는 모든 동아리 조회 API",
            description = "해당 멤버가 가입되어있는 모든 동아리 조회 API 입니다. accessToken과 함께 요청해주세요.(\"Bearer \"없이 토큰만 입력해주세요)"
    )
    public ResponseEntity<CustomResponse<List<MemberClubResponseDTO.MemberClubResponse>>> getClubs(HttpServletRequest request) {

        Long memberId = tokenProvider.getUserId(tokenProvider.resolveToken(request));
        List<MemberClubResponseDTO.MemberClubResponse> responses = memberClubService.findAllByMemberId(memberId);

        // 성공 응답 반환
        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, responses));
    }
}
