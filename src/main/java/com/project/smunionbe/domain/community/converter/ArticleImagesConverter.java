package com.project.smunionbe.domain.community.converter;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.community.dto.request.ArticleRequestDTO;
import com.project.smunionbe.domain.community.dto.response.ArticleResponseDTO;
import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.community.entity.ArticleImages;
import com.project.smunionbe.domain.member.entity.MemberClub;
import org.springframework.stereotype.Component;

@Component
public class ArticleImagesConverter {
    public ArticleImages toArticleImage(Article article, String image) {
        return ArticleImages.builder()
                .article(article)
                .imageUrl(image)
                .build();
    }
}
