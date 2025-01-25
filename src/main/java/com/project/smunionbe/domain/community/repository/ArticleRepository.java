package com.project.smunionbe.domain.community.repository;

import com.project.smunionbe.domain.community.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    // 좋아요
    @Modifying
    @Query("UPDATE Article a SET a.LikeNum = a.LikeNum + 1 WHERE a.id = :articleId")
    void increaseLikeCount(@Param("articleId") Long articleId);

    // 좋아요 취소
    @Modifying
    @Query("UPDATE Article a SET a.LikeNum = a.LikeNum - 1 WHERE a.id = :articleId AND a.LikeNum > 0")
    void decreaseLikeCount(@Param("articleId") Long articleId);
}
