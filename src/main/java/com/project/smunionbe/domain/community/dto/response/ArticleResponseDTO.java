package com.project.smunionbe.domain.community.dto.response;

import com.project.smunionbe.domain.community.entity.ArticleImages;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleResponseDTO {

    public record ArticleResponse(
            Long id,
            String departmentName,
            String clubName,
            String nickname,
            String title,
            String content,
            List<String> images,
            Integer likeNum,
            LocalDateTime createdAt
    ) {}
}