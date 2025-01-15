package com.project.smunionbe.domain.club.controller;

import com.project.smunionbe.domain.club.dto.request.ClubReqDTO;
import com.project.smunionbe.domain.club.dto.response.ClubResDTO;
import com.project.smunionbe.domain.club.dto.response.DepartmentResDTO;
import com.project.smunionbe.domain.club.service.command.ClubCommandService;
import com.project.smunionbe.domain.club.service.query.ClubQueryService;
import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.domain.notification.attendance.dto.request.AttendanceReqDTO;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/club")
@Tag(name = "동아리 API", description = "동아리 관련 CRUD 및 기능 API")
public class ClubController {

    private final ClubCommandService clubCommandService;
    private final ClubQueryService clubQueryService;

    @PostMapping("")
    @Operation(
            summary = "동아리 생성 API",
            description = "멤버가 동아리를 생성하는 API 입니다."
    )
    public CustomResponse<ClubResDTO.CreateClubDTO> createClub(
            @RequestBody @Valid ClubReqDTO.CreateClubDTO request,
            @AuthenticationPrincipal CustomUserDetails auth) {

        ClubResDTO.CreateClubDTO response = clubCommandService.createClub(request, auth.getMember().getId());

        return CustomResponse.onSuccess(response);

    }

    @PatchMapping("/modify")
    @Operation(
            summary = "동아리 수정 API",
            description = "기존 동아리 내용을 수정합니다."
    )
    public CustomResponse<String> updateClub(
            @RequestBody @Valid ClubReqDTO.UpdateClubRequest request,
            @AuthenticationPrincipal CustomUserDetails auth

    ) {
        clubCommandService.updateClub(request, auth.getMember().getId());
        return CustomResponse.onSuccess("동아리 수정 성공");
    }

    @PostMapping("/approve")
    @Operation(
            summary = "동아리 가입 API",
            description = "멤버가 동아리를 가입하는 API 입니다."
    )
    public CustomResponse<String> approveClub(
            @RequestBody @Valid ClubReqDTO.ApproveClubRequest request,
            @AuthenticationPrincipal CustomUserDetails auth
            ) {

        clubCommandService.approveClub(request, auth.getMember().getId());
        return CustomResponse.onSuccess("동아리 가입 성공");

    }

    @GetMapping("/{clubId}")
    @Operation(
            summary = "동아리 부원 조회 API",
            description = "동아리에 가입되어 있는 부원을 조회하는 API 입니다."
    )
    public CustomResponse<ClubResDTO.GetMemberClubListResDTO> getAllMemberClub(
           @PathVariable Long clubId,
           @AuthenticationPrincipal CustomUserDetails auth
    ) {

        ClubResDTO.GetMemberClubListResDTO response =
                clubQueryService.getAllMemberClubList(clubId, auth.getMember().getId());
        return CustomResponse.onSuccess(response);

    }
}
