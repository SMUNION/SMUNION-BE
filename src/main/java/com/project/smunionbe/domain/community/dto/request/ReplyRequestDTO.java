package com.project.smunionbe.domain.community.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ReplyRequestDTO {

    public record CreateReplyRequest(// 게시글 제목
            @NotBlank String body   // 게시글 내용
    ) {}
}
