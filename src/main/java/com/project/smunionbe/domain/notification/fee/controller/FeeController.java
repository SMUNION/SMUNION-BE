package com.project.smunionbe.domain.notification.fee.controller;

import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.domain.member.service.ClubSelectionService;
import com.project.smunionbe.domain.notification.fee.dto.request.FeeReqDTO;
import com.project.smunionbe.domain.notification.fee.dto.response.FeeResDTO;
import com.project.smunionbe.domain.notification.fee.service.command.FeeCommandService;
import com.project.smunionbe.domain.notification.fee.service.query.FeeQueryService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices/fees")
@Tag(name = "회비 공지 API", description = "회비 공지 생성 및 관련 기능 API")
@Slf4j
public class FeeController {

    private final FeeCommandService feeCommandService;
    private final FeeQueryService feeQueryService;
    private final ClubSelectionService clubSelectionService;

    @PostMapping("")
    @Operation(summary = "회비 공지 생성 API", description = "운영진이 새로운 회비 공지를 생성하는 API입니다.")
    public ResponseEntity<CustomResponse<String>> createFeeNotice(
            @RequestBody @Valid FeeReqDTO.CreateFeeNoticeRequestDTO reqDTO,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        feeCommandService.createFeeNotice(reqDTO, selectedMemberClubId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.onSuccess(HttpStatus.CREATED, "회비 공지가 성공적으로 생성되었습니다."));
    }

    @GetMapping("")
    @Operation(summary = "회비 공지 목록 조회 API", description = "특정 동아리의 회비 공지 목록을 커서 기반 페이지네이션으로 조회합니다.")
    public CustomResponse<FeeResDTO.FeeNoticeListResponse> getFeeNotices(
            @RequestParam("id") Long clubId,
            @RequestParam(value = "cursor", required = false) Long cursor,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        FeeResDTO.FeeNoticeListResponse response =
                feeQueryService.getFeeNotices(clubId, cursor, size, selectedMemberClubId);

        return CustomResponse.onSuccess(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "회비 공지 상세 조회 API", description = "특정 회비 공지의 상세 정보를 조회합니다.")
    public CustomResponse<FeeResDTO.FeeNoticeResponse> getFeeNoticeDetail(
            @PathVariable("id") Long feeId,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        FeeResDTO.FeeNoticeResponse response = feeQueryService.getFeeNoticeDetail(feeId, selectedMemberClubId);

        return CustomResponse.onSuccess(response);
    }
}
