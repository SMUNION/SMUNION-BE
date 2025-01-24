package com.project.smunionbe.domain.community.controller;

import com.project.smunionbe.domain.community.dto.request.ReplyRequestDTO;
import com.project.smunionbe.domain.community.dto.response.ArticleResponseDTO;
import com.project.smunionbe.domain.community.dto.response.ReplyResponseDTO;
import com.project.smunionbe.domain.community.service.ArticleService;
import com.project.smunionbe.domain.community.service.ReplyService;
import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.domain.member.service.ClubSelectionService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import com.project.smunionbe.global.config.jwt.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/community")
@Tag(name = "커뮤니티 댓글 API", description = "-")
public class ReplyController {
    private final TokenProvider tokenProvider;
    private final ClubSelectionService clubSelectionService;
    private final ReplyService replyService;


    @PostMapping("/{articleId}/replies")
    @Operation(
            summary = "댓글 작성 API",
            description = "댓글 작성 API"
    )
    public ResponseEntity<CustomResponse<ReplyResponseDTO.ReplyResponse>> createReply(@RequestBody ReplyRequestDTO.CreateReplyRequest dto, @PathVariable Long articleId, @AuthenticationPrincipal CustomUserDetails auth, HttpSession session) {
        //세션에서 memberClubId 가져오기
        Long memberId = auth.getMember().getId();
        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, memberId);

        // 댓글 생성
        ReplyResponseDTO.ReplyResponse response = replyService.createReply(dto, selectedMemberClubId, articleId);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.onSuccess(HttpStatus.CREATED, response));
    }

    @DeleteMapping("/{articleId}/replies/{replyId}")
    @Operation(summary = "댓글 삭제 API", description = "댓글 삭제 API입니다.")
    public ResponseEntity<CustomResponse<String>> deleteReply(@PathVariable Long articleId, @PathVariable Long replyId, @AuthenticationPrincipal CustomUserDetails auth, HttpSession session) {
        //세션에서 memberClubId 가져오기
        Long memberId = auth.getMember().getId();
        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, memberId);

        // 댓글 삭제
        replyService.deleteReply(articleId, replyId, selectedMemberClubId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, "댓글이 삭제되었습니다."));
    }

    @GetMapping("/{articleId}/replies")
    @Operation(summary = "게시글 댓글 전체 조회 API", description = "특정 게시글에 대한 댓글 목록을 조회하는 API입니다.")
    public ResponseEntity<CustomResponse<List<ReplyResponseDTO.ReplyResponse>>> getRepliesByArticleId(
            @PathVariable Long articleId) {

        //댓글 전체 조회
        List<ReplyResponseDTO.ReplyResponse> response = replyService.getRepliesByArticleId(articleId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, response));
    }



}
