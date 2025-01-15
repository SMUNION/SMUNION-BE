package com.project.smunionbe.domain.community.repository;

import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.community.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
