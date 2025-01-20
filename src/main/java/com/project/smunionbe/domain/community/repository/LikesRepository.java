package com.project.smunionbe.domain.community.repository;

import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.community.entity.Likes;
import com.project.smunionbe.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByArticleAndMember(Article article, Member member);
}
