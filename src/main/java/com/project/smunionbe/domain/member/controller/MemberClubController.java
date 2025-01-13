package com.project.smunionbe.domain.member.controller;

import com.project.smunionbe.domain.member.converter.MemberClubConverter;
import com.project.smunionbe.domain.member.dto.response.MemberClubResponseDTO;
import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.domain.member.service.ClubSelectionService;
import com.project.smunionbe.domain.member.service.MemberClubService;
import com.project.smunionbe.domain.member.service.MemberService;
import com.project.smunionbe.domain.member.service.TokenService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import com.project.smunionbe.global.config.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "동아리 회원 API", description = "-")
public class MemberClubController {

    private final MemberClubService memberClubService;
    private final TokenProvider tokenProvider;
    private final ClubSelectionService clubSelectionService;

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

    // 조회된 동아리 중 하나 선택하여 세션에 저장
    @PostMapping("/clubs/select")
    @Operation(
            summary = "특정 동아리 프로필 선택 API",
            description = "조회된 동아리 리스트 중 하나를 선택하여 세션에 저장하는 API 입니다."
    )
    public ResponseEntity<CustomResponse<MemberClubResponseDTO.MemberClubResponse>> selectClubProfile(@RequestParam Long memberClubId, HttpServletRequest request, HttpSession session) {
        Long memberId = tokenProvider.getUserId(tokenProvider.resolveToken(request));
        memberClubService.validateAndSetSelectedProfile(memberId, memberClubId, session);

        MemberClubResponseDTO.MemberClubResponse response = memberClubService.findById(memberClubId);

        // 성공 응답 반환
        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, response));
    }


    // 선택된 프로필 조회
    @GetMapping("/clubs/selected")
    @Operation(
            summary = "선택된 동아리 프로필 조회 API",
            description = "현재 세션에 저장된 선택된 동아리 프로필 정보를 조회하는 API"
    )
    public ResponseEntity<CustomResponse<MemberClubResponseDTO.MemberClubResponse>> getSelectedMemberClub(HttpServletRequest request, HttpSession session) {
        Long memberId = tokenProvider.getUserId(tokenProvider.resolveToken(request));
        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, memberId);

        MemberClubResponseDTO.MemberClubResponse response = memberClubService.findById(selectedMemberClubId);
        return ResponseEntity.ok(CustomResponse.onSuccess(HttpStatus.OK, response));
    }
}
