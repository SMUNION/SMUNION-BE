package com.project.smunionbe.domain.community.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ArticleRequestDTO {

    public record CreateArticleRequest(
            @NotBlank String title,      // 게시글 제목
            @NotBlank String content, // 게시글 내용
            @NotBlank int publicScope //공개 범위
    ) {}

    public record UpdateArticleRequest(
            String title,
            String content
    ) {}
}
