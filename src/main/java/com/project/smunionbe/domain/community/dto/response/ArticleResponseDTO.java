package com.project.smunionbe.domain.community.dto.response;

import java.time.LocalDateTime;

public class ArticleResponseDTO {

    public record ArticleResponse(
            Long id,
            String departmentName,
            String clubName,
            String nickname,
            String title,
            String content,
            Integer likeNum,
            LocalDateTime createdAt
    ) {}
}