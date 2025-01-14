package com.project.smunionbe.domain.notification.vote.repository;

import com.project.smunionbe.domain.notification.vote.entity.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {

    List<VoteItem> findAllByVoteNoticeId(Long voteNoticeId);
}
