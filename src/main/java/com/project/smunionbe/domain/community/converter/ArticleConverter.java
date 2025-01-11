package com.project.smunionbe.domain.community.converter;

import com.project.smunionbe.domain.community.dto.request.ArticleRequestDTO;
import com.project.smunionbe.domain.community.dto.response.ArticleResponseDTO;
import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.member.dto.request.MemberRequestDTO;
import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.entity.MemberClub;
import org.springframework.stereotype.Component;

@Component
public class ArticleConverter {
    public Article toArticle(ArticleRequestDTO.CreateArticleRequest dto, MemberClub memberClub) {
        return Article.builder()
                .memberClub(memberClub)
                .department("department")
                .memberName("memberName")
                .title(dto.title())
                .content(dto.content())
                .LikeNum(0)
                .build();
    }

    public ArticleResponseDTO.ArticleResponse toArticleResponseDto(Article article) {
        return new ArticleResponseDTO.ArticleResponse(article.getId(), article.getTitle(), article.getContent(), article.getLikeNum());
    }
}
