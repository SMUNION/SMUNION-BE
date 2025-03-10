package com.project.smunionbe.domain.member.controller;

import com.project.smunionbe.domain.member.dto.request.AccessTokenRequestDTO;
import com.project.smunionbe.domain.member.dto.request.MemberRequestDTO;
import com.project.smunionbe.domain.member.dto.response.AccessTokenResponseDTO;
import com.project.smunionbe.domain.member.dto.response.MemberResponseDTO;
import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.exception.AuthErrorCode;
import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.domain.member.service.MemberService;
import com.project.smunionbe.domain.member.service.RefreshTokenService;
import com.project.smunionbe.domain.member.service.TokenService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import com.project.smunionbe.global.config.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "회원 API", description = "-")
public class MemberController {
    private final TokenProvider tokenProvider;
    private final MemberService memberService;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    @Operation(
            summary = "회원가입 API",
            description = "회원가입 API 입니다."
    )
    public ResponseEntity<CustomResponse<String>> signup(@RequestBody MemberRequestDTO.CreateMemberDTO request) {
        memberService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.onSuccess(HttpStatus.CREATED, "회원가입이 완료되었습니다."));
    }




    // 로그인 메서드
    @PostMapping("/login")
    @Operation(
            summary = "로그인 API",
            description = "로그인 API입니다."
    )
    public ResponseEntity<CustomResponse<AccessTokenResponseDTO.ReturnTokenDTO>> login(@RequestBody MemberRequestDTO.LoginMemberDTO request) {
        // 1. 회원 이메일과 비밀번호로 인증
        Member member = memberService.authenticate(request.email(), request.password());

        // 2. 유효한 회원이라면 액세스 토큰 및 리프레시 토큰 생성
        String accessToken = tokenService.createNewAccessTokenForMember(member);
        String refreshToken = tokenService.createNewRefreshTokenForMember(member);

        // 3. 토큰들을 DTO에 저장
        AccessTokenResponseDTO.ReturnTokenDTO returnTokenDTO = new AccessTokenResponseDTO.ReturnTokenDTO("Bearer " + accessToken, refreshToken);

        // 4. 토큰 반환
        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, returnTokenDTO));
    }



    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃 API",
            description = "로그아웃 API 입니다. accessToken과 함께 요청해주세요.(\"Bearer \"없이 토큰만 입력해주세요)"
    )
    public ResponseEntity<CustomResponse<String>> logout(HttpServletRequest request) {
        String accessToken = tokenProvider.resolveToken(request);

        // 로그아웃 처리
        tokenService.logout(accessToken);

        // 성공 응답 반환
        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, "로그아웃에 성공하였습니다."));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "access 토큰 재발급 API",
            description = "access 토큰 재발급 API 입니다. refreshToken과 함께 요청해주세요.(header가 아닌 requestBody에 담아주세요)"
    )
    public ResponseEntity<CustomResponse<AccessTokenResponseDTO.ReturnTokenDTO>> refreshAccessToken(@RequestBody AccessTokenRequestDTO.CreateAccessTokenDTO dto) {
        //액세스 토큰 재발급
        Map<String, String> tokenMap = tokenService.createNewAccessToken(dto.refreshToken());

        String newAccessToken = tokenMap.get("accessToken");
        String newRefreshToken = tokenMap.get("refreshToken");

        // 토큰들을 DTO에 저장
        AccessTokenResponseDTO.ReturnTokenDTO returnTokenDTO = new AccessTokenResponseDTO.ReturnTokenDTO("Bearer " + newAccessToken, newRefreshToken);

        // 토큰 반환
        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, returnTokenDTO));

    }


    @GetMapping()
    @Operation(
            summary = "프로필 조회 API",
            description = "회원의 프로필을 조회하는 API 입니다."
    )
    public ResponseEntity<CustomResponse<MemberResponseDTO.MemberProfileResponse>> getProfile(@AuthenticationPrincipal CustomUserDetails auth) {
        //memberId 가져오기
        Long memberId = auth.getMember().getId();

        MemberResponseDTO.MemberProfileResponse response = memberService.getProfile(memberId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, response));
    }

    @DeleteMapping("/delete")
    @Operation(
            summary = "회원 탈퇴 API",
            description = "회원 탈퇴 API 입니다."
    )
    public ResponseEntity<CustomResponse<String>> deleteAccount(@AuthenticationPrincipal CustomUserDetails auth) {
        //memberId 가져오기
        Long memberId = auth.getMember().getId();

        memberService.deleteMember(memberId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, "회원 탈퇴가 완료되었습니다."));
    }


    @PatchMapping("/password")
    @Operation(
            summary = "비밀번호 변경 API",
            description = "현재 비밀번호를 검증 후 새 비밀번호로 변경하는 API입니다."
    )
    public ResponseEntity<CustomResponse<String>> changePassword(@AuthenticationPrincipal CustomUserDetails auth, HttpServletRequest request,@RequestBody @Valid MemberRequestDTO.ChangePasswordDTO dto) {
        Long memberId = auth.getMember().getId();

        memberService.changePassword(memberId, dto);

        // 로그아웃 처리
        String accessToken = tokenProvider.resolveToken(request);
        if (accessToken != null) {
            tokenService.logout(accessToken);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, "비밀번호가 성공적으로 변경되었습니다."));
    }

    @PatchMapping("/profile-image")
    @Operation(
            summary = "프로필 사진 등록/수정 API",
            description = "회원의 프로필 사진을 등록하거나 수정하는 API입니다."
    )
    public ResponseEntity<CustomResponse<MemberResponseDTO.MemberProfileImageResponse>> updateProfileImage(
            @AuthenticationPrincipal CustomUserDetails auth,
            @RequestPart(value = "image", required = true) MultipartFile image) {

        Long memberId = auth.getMember().getId();
        MemberResponseDTO.MemberProfileImageResponse response = memberService.updateProfileImage(memberId, image);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, response));
    }

    @GetMapping("/profile-image")
    @Operation(
            summary = "프로필 사진 조회 API",
            description = "회원의 프로필 사진을 조회하는 API입니다."
    )
    public ResponseEntity<CustomResponse<MemberResponseDTO.MemberProfileImageResponse>> getProfileImage(
            @AuthenticationPrincipal CustomUserDetails auth) {

        Long memberId = auth.getMember().getId();
        MemberResponseDTO.MemberProfileImageResponse response = memberService.getProfileImage(memberId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, response));
    }

    @DeleteMapping("/profile-image")
    @Operation(
            summary = "프로필 사진 삭제 API",
            description = "회원의 프로필 사진을 삭제하는 API입니다."
    )
    public ResponseEntity<CustomResponse<String>> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails auth) {

        Long memberId = auth.getMember().getId();
        memberService.deleteProfileImage(memberId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, "프로필 사진이 삭제되었습니다."));
    }

    @PostMapping("/find-password")
    @Operation(
            summary = "비밀번호 찾기 API",
            description = "등록된 이메일로 임시 비밀번호를 전송합니다."
    )
    public ResponseEntity<CustomResponse<String>> findPassword(@RequestBody @Valid MemberRequestDTO.FindPasswordDTO dto) {
        memberService.findPassword(dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, "임시 비밀번호가 이메일로 전송되었습니다."));
    }



}
