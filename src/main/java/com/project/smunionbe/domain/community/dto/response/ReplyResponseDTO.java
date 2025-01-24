package com.project.smunionbe.domain.community.dto.response;

import java.time.LocalDateTime;

public class ReplyResponseDTO {

    public record ReplyResponse(
            Long id,
            Long articleId,
            String departmentName,
            String clubName,
            String nickname,
            String body,
            LocalDateTime createdAt
    ) {}
}