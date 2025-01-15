package com.project.smunionbe.domain.community.controller;

import com.project.smunionbe.domain.community.dto.request.ReplyRequestDTO;
import com.project.smunionbe.domain.community.dto.response.ArticleResponseDTO;
import com.project.smunionbe.domain.community.dto.response.ReplyResponseDTO;
import com.project.smunionbe.domain.community.service.ArticleService;
import com.project.smunionbe.domain.community.service.ReplyService;
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
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<CustomResponse<ReplyResponseDTO.ReplyResponse>> createReply(@RequestBody ReplyRequestDTO.CreateReplyRequest dto, @PathVariable Long articleId, HttpServletRequest request, HttpSession session) {
        //세션에서 memberClubId 가져오기
        Long memberId = tokenProvider.getUserId(tokenProvider.resolveToken(request));
        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, memberId);

        // 댓글 생성
        ReplyResponseDTO.ReplyResponse response = replyService.createReply(dto, selectedMemberClubId, articleId);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.onSuccess(HttpStatus.CREATED, response));
    }

}
