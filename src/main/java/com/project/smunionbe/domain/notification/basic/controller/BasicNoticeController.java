package com.project.smunionbe.domain.notification.basic.controller;

import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.domain.member.service.ClubSelectionService;
import com.project.smunionbe.domain.notification.basic.dto.request.BasicNoticeReqDTO;
import com.project.smunionbe.domain.notification.basic.dto.response.BasicNoticeResDTO;
import com.project.smunionbe.domain.notification.basic.service.command.BasicNoticeCommandService;
import com.project.smunionbe.domain.notification.basic.service.query.BasicNoticeQueryService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notices/basic")
@Tag(name = "일반 공지 API", description = "일반 공지 관련 CRUD 및 기능 API")
public class BasicNoticeController {

    private final BasicNoticeCommandService basicNoticeCommandService;
    private final BasicNoticeQueryService basicNoticeQueryService;
    private final ClubSelectionService clubSelectionService;

    @PostMapping("")
    @Operation(summary = "일반 공지 생성 API", description = "새로운 일반 공지를 생성합니다.")
    public ResponseEntity<CustomResponse<String>> createBasicNotice(
            @RequestBody @Valid BasicNoticeReqDTO.CreateBasicNoticeRequest request,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        basicNoticeCommandService.createBasicNotice(request, selectedMemberClubId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.onSuccess(HttpStatus.CREATED, "일반 공지가 성공적으로 생성되었습니다."));
    }

    @GetMapping("")
    @Operation(summary = "일반 공지 목록 조회 API", description = "특정 동아리의 모든 일반 공지를 커서 기반 페이지네이션으로 조회합니다.")
    @Parameters({
            @Parameter(name = "cursor", description = "마지막 데이터의 기준 커서 값"),
            @Parameter(name = "size", description = "한 번에 가져올 데이터의 개수")
    })
    public CustomResponse<BasicNoticeResDTO.BasicNoticeListResponse> getBasicNotices(
            @RequestParam(value = "cursor", required = false) Long cursor,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        BasicNoticeResDTO.BasicNoticeListResponse response = basicNoticeQueryService.getBasicNotices(cursor, size, selectedMemberClubId);

        return CustomResponse.onSuccess(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "일반 공지 상세 조회 API", description = "특정 일반 공지의 상세 정보를 조회합니다.")
    public CustomResponse<BasicNoticeResDTO.BasicNoticeDetailResponse> getBasicNoticeDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        BasicNoticeResDTO.BasicNoticeDetailResponse response = basicNoticeQueryService.getBasicNoticeDetail(id, selectedMemberClubId);

        return CustomResponse.onSuccess(response);
    }

    @GetMapping("/{id}/unread")
    @Operation(summary = "일반 공지 미확인 인원 조회 API", description = "해당 일반 공지를 확인하지 않은 멤버를 조회합니다.")
    public CustomResponse<BasicNoticeResDTO.UnreadMembersResponse> getUnreadMembers(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        BasicNoticeResDTO.UnreadMembersResponse response = basicNoticeQueryService.getUnreadMembers(id, selectedMemberClubId);

        return CustomResponse.onSuccess(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "일반 공지 수정 API", description = "기존 일반 공지의 내용을 수정합니다.")
    public CustomResponse<String> updateBasicNotice(
            @PathVariable Long id,
            @RequestBody @Valid BasicNoticeReqDTO.UpdateBasicNoticeRequest request,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        basicNoticeCommandService.updateBasicNotice(id, request, selectedMemberClubId);

        return CustomResponse.onSuccess("일반 공지가 성공적으로 수정되었습니다.");
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "일반 공지 읽음 상태 업데이트 API", description = "특정 일반 공지에 대해 사용자의 읽음 상태를 업데이트합니다.")
    public CustomResponse<String> markNoticeAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        basicNoticeCommandService.markNoticeAsRead(id, selectedMemberClubId);

        return CustomResponse.onSuccess("공지 읽음 상태가 업데이트되었습니다.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "일반 공지 삭제 API", description = "해당 공지를 삭제합니다.")
    public CustomResponse<String> deleteBasicNotice(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        basicNoticeCommandService.deleteBasicNotice(id, selectedMemberClubId);

        return CustomResponse.onSuccess("일반 공지가 성공적으로 삭제되었습니다.");
    }
}
