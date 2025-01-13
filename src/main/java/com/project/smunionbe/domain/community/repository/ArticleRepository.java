package com.project.smunionbe.domain.community.repository;

import com.project.smunionbe.domain.community.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
