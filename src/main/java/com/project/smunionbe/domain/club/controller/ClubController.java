package com.project.smunionbe.domain.club.controller;

import com.project.smunionbe.domain.club.dto.request.ClubReqDTO;
import com.project.smunionbe.domain.club.dto.response.ClubResDTO;
import com.project.smunionbe.domain.club.service.command.ClubCommandService;
import com.project.smunionbe.domain.notification.attendance.dto.request.AttendanceReqDTO;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/club")
@Tag(name = "동아리 API", description = "동아리 관련 CRUD 및 기능 API")
public class ClubController {

    private final ClubCommandService clubCommandService;

    @PostMapping("/{email}")
    @Operation(
            summary = "동아리 생성 API",
            description = "멤버가 동아리를 생성하는 API 입니다."
    )
    public CustomResponse<ClubResDTO.CreateClubDTO> createClub(
            @RequestBody @Valid ClubReqDTO.CreateClubDTO request,
            @PathVariable String email) {

        ClubResDTO.CreateClubDTO response = clubCommandService.createClub(request, email);

        return CustomResponse.onSuccess(response);

    }

    @PatchMapping("/modify/{clubId}")
    @Operation(
            summary = "동아리 수정 API",
            description = "기존 동아리 내용을 수정합니다."
    )
    public CustomResponse<String> updateClub(
            @PathVariable Long clubId,
            @RequestBody @Valid ClubReqDTO.UpdateClubRequest request
    ) {
        clubCommandService.updateClub(clubId, request);
        return CustomResponse.onSuccess("동아리 수정 성공");
    }

}
