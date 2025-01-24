package com.project.smunionbe.domain.community.controller;

import com.project.smunionbe.domain.community.dto.request.ArticleRequestDTO;
import com.project.smunionbe.domain.community.dto.response.ArticleResponseDTO;
import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.community.service.ArticleService;
import com.project.smunionbe.domain.member.dto.request.MemberRequestDTO;
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
@Tag(name = "커뮤니티 게시글 API", description = "-")
public class ArticleController {
    private final ArticleService articleService;
    private final TokenProvider tokenProvider;
    private final ClubSelectionService clubSelectionService;

    @PostMapping()
    @Operation(
            summary = "게시글 작성 API",
            description = "게시글 작성 API"
    )
    public ResponseEntity<CustomResponse<ArticleResponseDTO.ArticleResponse>> createArticle(@RequestBody ArticleRequestDTO.CreateArticleRequest dto, @AuthenticationPrincipal CustomUserDetails auth, HttpSession session) {
        //세션에서 memberClubId 가져오기
        Long memberId = auth.getMember().getId();
        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, memberId);

        // 게시글 생성
        ArticleResponseDTO.ArticleResponse response = articleService.createArticle(dto, selectedMemberClubId);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.onSuccess(HttpStatus.CREATED, response));
    }

    @GetMapping("/{articleId}")
    @Operation(
            summary = "게시글 단건 조회 API",
            description = "게시글 단건 조회 API"
    )
    public ResponseEntity<CustomResponse<ArticleResponseDTO.ArticleResponse>> getArticle(
            @PathVariable Long articleId) {

        // 게시글 조회
        ArticleResponseDTO.ArticleResponse response = articleService.getArticle(articleId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, response));
    }

    @GetMapping
    @Operation(
            summary = "게시글 전체 조회 API",
            description = "게시글을 최신순으로 전체 조회하는 API입니다."
    )
    public ResponseEntity<CustomResponse<List<ArticleResponseDTO.ArticleResponse>>> getAllArticles() {
        // 서비스 호출하여 게시글 전체 조회
        List<ArticleResponseDTO.ArticleResponse> response = articleService.getAllArticles();

        return ResponseEntity.ok(CustomResponse.onSuccess(HttpStatus.OK, response));
    }

    @PatchMapping("/{articleId}")
    @Operation(
            summary = "게시글 수정 API",
            description = "게시글 수정 API입니다. 제목과 내용 중 수정된 부분만 request로 보내면 됩니다."
    )
    public ResponseEntity<CustomResponse<ArticleResponseDTO.ArticleResponse>> updateArticle(@PathVariable Long articleId, @RequestBody ArticleRequestDTO.UpdateArticleRequest request, @AuthenticationPrincipal CustomUserDetails auth, HttpSession session) {
        //세션에서 memberClubId 가져오기
        Long memberId = auth.getMember().getId();
        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, memberId);

        ArticleResponseDTO.ArticleResponse response = articleService.updateArticle(articleId, selectedMemberClubId, request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, response));
    }


    @DeleteMapping("/{articleId}")
    @Operation(summary = "게시글 삭제 API", description = "게시글을 삭제하는 API입니다.")
    public ResponseEntity<CustomResponse<String>> deleteArticle(@PathVariable Long articleId, @AuthenticationPrincipal CustomUserDetails auth, HttpSession session) {
        // 세션에서 memberClubId 가져오기
        Long memberId = auth.getMember().getId();
        Long selectedMemberClubId = clubSelectionService.getSelectedProfile(session, memberId);

        // 게시글 삭제
        articleService.deleteArticle(articleId, selectedMemberClubId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CustomResponse.onSuccess(HttpStatus.OK, "게시글이 삭제되었습니다."));
    }



}
