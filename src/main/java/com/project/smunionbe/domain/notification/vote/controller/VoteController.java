package com.project.smunionbe.domain.notification.vote.controller;

import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.domain.member.service.ClubSelectionService;
import com.project.smunionbe.domain.notification.vote.dto.request.VoteReqDTO;
import com.project.smunionbe.domain.notification.vote.dto.response.VoteResDTO;
import com.project.smunionbe.domain.notification.vote.service.command.VoteCommandService;
import com.project.smunionbe.domain.notification.vote.service.query.VoteQueryService;
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
@RequestMapping("/api/v1/notices/votes")
@Tag(name = "투표 공지 API", description = "투표 공지 관련 CRUD 및 기능 API")
public class VoteController {

    private final VoteCommandService voteCommandService;
    private final VoteQueryService voteQueryService;
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

    @GetMapping("")
    @Operation(summary = "투표 공지 목록 조회 API", description = "특정 동아리의 투표 공지 목록을 커서 기반 페이지네이션으로 조회합니다.")
    @Parameters({
            @Parameter(name = "cursor", description = "마지막 데이터의 기준 커서 값"),
            @Parameter(name = "size", description = "한 번에 가져올 데이터의 개수")
    })
    public CustomResponse<VoteResDTO.VoteListResponse> getVotes(
            @RequestParam(value = "cursor", required = false) Long cursor,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        VoteResDTO.VoteListResponse response = voteQueryService.getVotes(cursor, size, selectedMemberClubId);

        return CustomResponse.onSuccess(response);
    }

    @GetMapping("/{voteId}")
    @Operation(summary = "투표 공지 상세 조회 API", description = "특정 투표 공지의 상세 정보를 조회합니다.")
    @Parameter(name = "voteId", description = "조회할 공지의 ID", required = true)
    public CustomResponse<VoteResDTO.VoteDetailResponse> getVoteDetail(
            @PathVariable("voteId") Long voteId,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        VoteResDTO.VoteDetailResponse response = voteQueryService.getVoteDetail(voteId, selectedMemberClubId);

        return CustomResponse.onSuccess(response);
    }

    @GetMapping("/{id}/results")
    @Operation(summary = "투표 현황 조회 API", description = "특정 투표 공지의 투표 현황을 조회합니다.")
    @Parameter(name = "id", description = "투표 공지 ID", required = true)
    public CustomResponse<VoteResDTO.VoteResultResponse> getVoteResults(
            @PathVariable("id") Long voteId,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        VoteResDTO.VoteResultResponse response = voteQueryService.getVoteResults(voteId, selectedMemberClubId);

        return CustomResponse.onSuccess(response);
    }

    @GetMapping("/{id}/absentees")
    @Operation(summary = "미참여 멤버 조회 API", description = "특정 투표 공지에 대해 투표하지 않은 멤버를 조회합니다.")
    @Parameter(name = "id", description = "투표 공지 ID", required = true)
    public CustomResponse<VoteResDTO.VoteAbsenteesResponse> getAbsentees(
            @PathVariable("id") Long voteId,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        VoteResDTO.VoteAbsenteesResponse response = voteQueryService.getAbsentees(voteId, selectedMemberClubId);

        return CustomResponse.onSuccess(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "투표 공지 수정 API", description = "투표 공지 내용을 수정합니다.")
    @Parameter(name = "id", description = "수정할 투표 공지의 ID", required = true)
    public CustomResponse<String> updateVote(
            @PathVariable("id") Long voteId,
            @RequestBody @Valid VoteReqDTO.UpdateVoteDTO reqDTO,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        voteCommandService.updateVote(voteId, reqDTO, selectedMemberClubId);

        return CustomResponse.onSuccess(HttpStatus.OK, "투표 공지가 성공적으로 수정되었습니다.");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "투표 공지 삭제 API", description = "특정 투표 공지를 삭제합니다.")
    @Parameter(name = "id", description = "삭제할 투표 공지의 ID", required = true)
    public ResponseEntity<CustomResponse<String>> deleteVote(
            @PathVariable("id") Long voteId,
            @AuthenticationPrincipal CustomUserDetails authMember,
            HttpSession session) {

        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, authMember.getMember().getId());
        voteCommandService.deleteVoteNotice(voteId, selectedMemberClubId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, "투표 공지가 성공적으로 삭제되었습니다."));
    }
}
