package com.project.smunionbe.domain.community.controller;

import com.project.smunionbe.domain.community.dto.request.ArticleRequestDTO;
import com.project.smunionbe.domain.community.dto.response.ArticleResponseDTO;
import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.community.service.ArticleService;
import com.project.smunionbe.domain.member.dto.request.MemberRequestDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/community")
@Tag(name = "커뮤니티 게시글 API", description = "-")
public class ArticleController {
    private final ArticleService articleService;
    private final TokenProvider tokenProvider;
    private final ClubSelectionService clubSelectionService;

    @PostMapping()
    @Operation(
            summary = "게시글 작성 API",
            description = "게시글 작성 API"
    ) //여기에 이어서 작성
    public ResponseEntity<CustomResponse<ArticleResponseDTO.ArticleResponse>> createArticle(@RequestBody ArticleRequestDTO.CreateArticleRequest dto, HttpServletRequest request, HttpSession session) {
        //세션에서 memberClubId 가져오기
        Long memberId = tokenProvider.getUserId(tokenProvider.resolveToken(request));
        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, memberId);

        // 게시글 생성
        ArticleResponseDTO.ArticleResponse response = articleService.createArticle(dto, selectedMemberClubId);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.onSuccess(HttpStatus.CREATED, response));
    }

}
