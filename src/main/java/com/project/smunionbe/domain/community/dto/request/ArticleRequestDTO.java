package com.project.smunionbe.domain.community.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ArticleRequestDTO {

    public record CreateArticleRequest(
            @NotBlank Long memberClubId,
            @NotBlank String title,      // 게시글 제목
            @NotBlank String content   // 게시글 내용
    ) {}
}
