package com.project.smunionbe.domain.community.repository;

import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.community.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    Optional<Reply> findByIdAndArticleId(Long replyId, Long articleId); // 게시글 ID 추가 검증

    List<Reply> findByArticleIdOrderByCreatedAtAsc(Long articleId);
}
