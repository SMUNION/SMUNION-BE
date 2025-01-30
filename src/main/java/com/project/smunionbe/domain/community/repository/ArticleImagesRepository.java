package com.project.smunionbe.domain.community.repository;

import com.project.smunionbe.domain.community.entity.ArticleImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


public interface ArticleImagesRepository extends JpaRepository<ArticleImages, Long> {
    // 특정 게시글(article_id)의 모든 이미지 URL 조회
    @Query("SELECT ai.imageUrl FROM ArticleImages ai WHERE ai.article.id = :articleId")
    List<String> findImageUrlsByArticleId(@Param("articleId") Long articleId);

    // 게시글 전체 조회시 최적화
    @Query("SELECT ai.article.id, ai.imageUrl FROM ArticleImages ai WHERE ai.article.id IN :articleIds")
    List<Object[]> findImageUrlsByArticleIds(@Param("articleIds") List<Long> articleIds);

    @Transactional
    void deleteByArticleId(Long articleId);

}
