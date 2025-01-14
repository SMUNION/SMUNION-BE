package com.project.smunionbe.domain.notification.vote.controller;

import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.domain.member.service.ClubSelectionService;
import com.project.smunionbe.domain.notification.vote.dto.request.VoteReqDTO;
import com.project.smunionbe.domain.notification.vote.service.command.VoteCommandService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/notices/votes")
@Tag(name = "투표 공지 API", description = "투표 공지 관련 CRUD 및 기능 API")
public class VoteController {

    private final VoteCommandService voteCommandService;
    private final ClubSelectionService clubSelectionService;

    @PostMapping("")
    @Operation(summary = "투표 공지 생성 API", description = "새로운 투표 공지를 생성합니다.")
    public ResponseEntity<CustomResponse<String>> createVote(
            @RequestBody VoteReqDTO.CreateVoteDTO reqDTO,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        voteCommandService.createVoteNotice(reqDTO, selectedMemberClubId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.onSuccess(HttpStatus.CREATED, "투표 공지가 성공적으로 생성되었습니다."));
    }

    @PostMapping("/{id}/participate")
    @Operation(summary = "투표 참여 API", description = "해당 투표에 참여합니다.")
    public ResponseEntity<CustomResponse<String>> participateVote(
            @PathVariable Long id,
            @RequestBody @Valid VoteReqDTO.ParticipateVoteDTO reqDTO,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        voteCommandService.participateVote(id, reqDTO, selectedMemberClubId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.onSuccess(HttpStatus.CREATED, "투표가 성공적으로 등록되었습니다."));
    }
}
