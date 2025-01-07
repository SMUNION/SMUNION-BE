package com.project.smunionbe.domain.club.controller;

import com.project.smunionbe.domain.club.dto.request.ClubReqDTO;
import com.project.smunionbe.domain.club.dto.request.GalleryReqDTO;
import com.project.smunionbe.domain.club.dto.response.ClubResDTO;
import com.project.smunionbe.domain.club.dto.response.GalleryResDTO;
import com.project.smunionbe.domain.club.service.command.GalleryCommandService;
import com.project.smunionbe.domain.club.service.query.GalleryQueryService;
import com.project.smunionbe.domain.notification.attendance.dto.response.AttendanceResDTO;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/gallery")
@Tag(name = "갤러리 API", description = "갤러리 관련 CRUD 및 기능 API")
public class GalleryController {
    private final GalleryCommandService galleryCommandService;
    private final GalleryQueryService galleryQueryService;
    @PostMapping("/create/{clubId}")
    @Operation(
            summary = "갤러리 생성 API",
            description = "동아리 멤버가 갤러리를 생성하는 API 입니다."
    )
    public CustomResponse<GalleryResDTO.CreateGalleryResDTO> createGallery(
            @RequestBody @Valid GalleryReqDTO.CreateGalleryDTO request,
            @PathVariable Long clubId
            ) {

        GalleryResDTO.CreateGalleryResDTO response = galleryCommandService.createGallery(request, clubId);

        return CustomResponse.onSuccess(response);

    }

    @PatchMapping("/modify/{memberId}/{galleryId}")
    @Operation(
            summary = "갤러리 수정 API",
            description = "기존 갤러리 내용을 수정합니다."
    )
    public CustomResponse<String> updateGallery(
            @PathVariable Long galleryId,
            @PathVariable Long memberId,
            @RequestBody @Valid GalleryReqDTO.UpdateGalleryRequest request
    ) {
        galleryCommandService.updateGallery(galleryId, request, memberId);
        return CustomResponse.onSuccess("동아리 수정 성공");
    }

    @GetMapping("/{memberId}/{galleryId}")
    @Operation(
            summary = "갤러리 조회 API",
            description = "특정 갤러리 정보를 조회합니다."
    )
    public CustomResponse<GalleryResDTO.GetGalleryResDTO> getGallery(
            @PathVariable Long galleryId,
            @PathVariable Long memberId
    ) {
        GalleryResDTO.GetGalleryResDTO response = galleryQueryService.getGallery(galleryId, memberId);
        return CustomResponse.onSuccess(response);
    }

    @GetMapping("/getAll/{clubId}/{memberId}")
    @Operation(
            summary = "갤러리 전제 조회 API",
            description = "특정 동아리의 모든 갤러리 내용을 조회합니다."
    )

    public CustomResponse<GalleryResDTO.GetGalleryListResDTO> getAllGallery(
            @PathVariable Long clubId,
            @PathVariable Long memberId
    ) {
        GalleryResDTO.GetGalleryListResDTO response =
                galleryQueryService.getAllGallery(clubId, memberId);
        return CustomResponse.onSuccess(response);
    }

    @DeleteMapping("/{memberId}/{galleryId}")
    @Operation(
            summary = "갤러리 삭제 API",
            description = "특정 갤러리를 삭제합니다."
    )
    public CustomResponse<String> deleteGallery(
            @PathVariable Long galleryId,
            @PathVariable Long memberId
    ) {
        galleryCommandService.deleteGallery(galleryId, memberId);
        return CustomResponse.onSuccess("갤러리가 성공적으로 삭제되었습니다.");
    }

}
