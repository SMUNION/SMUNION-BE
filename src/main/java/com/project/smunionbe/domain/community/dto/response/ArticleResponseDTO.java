package com.project.smunionbe.domain.community.dto.response;

public class ArticleResponseDTO {

    public record ArticleResponse(
            Long id,
            String title,
            String content,
            Integer likeNum
    ) {}
}