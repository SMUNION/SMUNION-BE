package com.project.smunionbe.domain.club.controller;

import com.project.smunionbe.domain.club.dto.request.ClubReqDTO;
import com.project.smunionbe.domain.club.dto.response.ClubResDTO;
import com.project.smunionbe.domain.club.dto.response.DepartmentResDTO;
import com.project.smunionbe.domain.club.service.command.ClubCommandService;
import com.project.smunionbe.domain.club.service.query.ClubQueryService;
import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.domain.member.service.ClubSelectionService;
import com.project.smunionbe.domain.notification.attendance.dto.request.AttendanceReqDTO;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/club")
@Tag(name = "동아리 API", description = "동아리 관련 CRUD 및 기능 API")
public class ClubController {

    private final ClubCommandService clubCommandService;
    private final ClubQueryService clubQueryService;
    private final ClubSelectionService clubSelectionService;


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(
            summary = "동아리 생성 API",
            description = "동아리를 생성합니다."
    )
    public CustomResponse<ClubResDTO.CreateClubDTO> createClub(
            @RequestPart(value = "name") String name,
            @RequestPart(value = "description") String description,
            @AuthenticationPrincipal CustomUserDetails auth,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        ClubReqDTO.CreateClubDTO request = new ClubReqDTO.CreateClubDTO(name, description);
        ClubResDTO.CreateClubDTO response = clubCommandService.createClub(request, auth.getMember().getId(), image);
        return CustomResponse.onSuccess(response);
    }

    @PatchMapping(path = "/modify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "동아리 수정 API",
            description = "기존 동아리 내용을 수정합니다."
    )
    public CustomResponse<String> updateClub(
            @RequestPart(value = "name") String name,
            @RequestPart(value = "description") String description,
            @AuthenticationPrincipal CustomUserDetails auth,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpSession session

    ) {
        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, auth.getMember().getId());
        ClubReqDTO.UpdateClubRequest request = new ClubReqDTO.UpdateClubRequest(name, description);
        clubCommandService.updateClub(request, selectedMemberClubId, image);
        return CustomResponse.onSuccess("동아리 수정 성공");
    }

    @PostMapping("/approve")
    @Operation(
            summary = "동아리 가입 API",
            description = "멤버가 동아리를 가입하는 API 입니다."
    )
    public CustomResponse<String> approveClub(
            @RequestBody @Valid ClubReqDTO.ApproveClubRequest request,
            @AuthenticationPrincipal CustomUserDetails auth,
            HttpSession session

    ) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, auth.getMember().getId());

        clubCommandService.approveClub(request, auth.getMember().getId(), selectedMemberClubId);
        return CustomResponse.onSuccess("동아리 가입 성공");

    }

    @GetMapping("")
    @Operation(
            summary = "동아리 부원 조회 API",
            description = "동아리에 가입되어 있는 부원을 조회하는 API 입니다."
    )
    public CustomResponse<ClubResDTO.GetMemberClubListResDTO> getAllMemberClub(
           @AuthenticationPrincipal CustomUserDetails auth,
           HttpSession session
    ) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, auth.getMember().getId());

        ClubResDTO.GetMemberClubListResDTO response =
                clubQueryService.getAllMemberClubList(selectedMemberClubId);
        return CustomResponse.onSuccess(response);

    }

    @GetMapping("/detail")
    @Operation(
            summary = "동아리 상세 조회 API",
            description = "동아리의 전체적인 정보를 조회하는 API 입니다."
    )
    public CustomResponse<ClubResDTO.ClubDetailResDTO> getClubDetail(
            @AuthenticationPrincipal CustomUserDetails auth,
            HttpSession session
    ) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, auth.getMember().getId());

        ClubResDTO.ClubDetailResDTO response =
                clubQueryService.getclubDetailResDTO(selectedMemberClubId);

        return CustomResponse.onSuccess(response);

    }
}
