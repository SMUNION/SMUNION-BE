package com.project.smunionbe.domain.notification.fee.controller;

import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.domain.member.service.ClubSelectionService;
import com.project.smunionbe.domain.notification.fee.dto.request.FeeReqDTO;
import com.project.smunionbe.domain.notification.fee.service.command.FeeCommandService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices/fees")
@Tag(name = "회비 공지 API", description = "회비 공지 생성 및 관련 기능 API")
@Slf4j
public class FeeController {

    private final FeeCommandService feeCommandService;
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
}
