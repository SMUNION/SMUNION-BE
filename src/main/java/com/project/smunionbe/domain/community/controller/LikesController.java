package com.project.smunionbe.domain.community.controller;

import com.project.smunionbe.domain.community.service.LikesService;
import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/community")
@Tag(name = "커뮤니티 게시글 좋아요 API", description = "좋아요/좋아요 취소 기능 & 좋아요 여부 확인 API")
public class LikesController {
    private final LikesService likesService;

    @PatchMapping("/{articleId}/likes")
    @Operation(
            summary = "좋아요 / 좋아요 취소 API",
            description = "좋아요 / 좋아요 취소 API"
    )
    public ResponseEntity<CustomResponse<String>> toggleLikes(@PathVariable Long articleId, @AuthenticationPrincipal CustomUserDetails auth) {
        boolean isLiked = likesService.toggleLike(articleId, auth.getMember().getId());

        if (isLiked) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomResponse.onSuccess(HttpStatus.CREATED, "게시글에 좋아요를 추가했습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(CustomResponse.onSuccess(HttpStatus.OK, "게시글에 좋아요를 취소했습니다."));
        }
    }
    @GetMapping("/{articleId}/likes")
    @Operation(
            summary = "좋아요 여부 확인 API",
            description = "좋아요 여부 확인 API"
    )
    public ResponseEntity<CustomResponse<Boolean>> checkLikeStatus(
            @PathVariable Long articleId,
            @AuthenticationPrincipal CustomUserDetails auth) {

        boolean isLiked = likesService.isLiked(articleId, auth.getMember().getId());
        if (isLiked) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(CustomResponse.onSuccess(HttpStatus.OK, true));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(CustomResponse.onSuccess(HttpStatus.OK, false));
        }
    }
}


