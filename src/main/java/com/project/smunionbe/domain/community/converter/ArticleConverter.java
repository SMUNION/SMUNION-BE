package com.project.smunionbe.domain.community.converter;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.community.dto.request.ArticleRequestDTO;
import com.project.smunionbe.domain.community.dto.response.ArticleResponseDTO;
import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.community.entity.ArticleImages;
import com.project.smunionbe.domain.member.dto.request.MemberRequestDTO;
import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.entity.MemberClub;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArticleConverter {
    public Article toArticle(ArticleRequestDTO.CreateArticleRequest dto, MemberClub memberClub, Club club, Department department) {
        return Article.builder()
                .memberClub(memberClub)
                .department(department.getName())
                .memberName(club.getName())
                .title(dto.title())
                .content(dto.content())
                .LikeNum(0)
                .publicScope(dto.publicScope())
                .build();
    }

    public ArticleResponseDTO.ArticleResponse toArticleResponseDto(Article article, List<String> imageUrls, String clubName, String departmentName, String nickname) {
        return new ArticleResponseDTO.ArticleResponse(article.getId(), clubName, departmentName, nickname, article.getTitle(), article.getContent(), imageUrls, article.getLikeNum(), article.getCreatedAt());
    }
}
