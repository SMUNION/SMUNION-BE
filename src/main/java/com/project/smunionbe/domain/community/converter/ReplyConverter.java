package com.project.smunionbe.domain.community.converter;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.community.dto.request.ArticleRequestDTO;
import com.project.smunionbe.domain.community.dto.request.ReplyRequestDTO;
import com.project.smunionbe.domain.community.dto.response.ArticleResponseDTO;
import com.project.smunionbe.domain.community.dto.response.ReplyResponseDTO;
import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.community.entity.Reply;
import com.project.smunionbe.domain.member.entity.MemberClub;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReplyConverter {
    public Reply toReply(ReplyRequestDTO.CreateReplyRequest dto, Article article, MemberClub memberClub) {
        return Reply.builder()
                .memberClub(memberClub)
                .article(article)
                .body(dto.body())
                .build();
    }

    public ReplyResponseDTO.ReplyResponse toReplyResponseDto(Reply reply, String clubName, String departmentName, String nickname) {
        return new ReplyResponseDTO.ReplyResponse(reply.getId(), reply.getArticle().getId(), clubName, departmentName, nickname, reply.getBody(), reply.getCreatedAt());
    }

    public List<ReplyResponseDTO.ReplyResponse> toReplyResponseDtoList(List<Reply> replies) {
        return replies.stream()
                .map(reply -> toReplyResponseDto(
                        reply,
                        reply.getMemberClub().getClub().getName(),
                        reply.getArticle().getDepartment(),
                        reply.getMemberClub().getNickname()
                ))
                .toList();
    }
}
