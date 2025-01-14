package com.project.smunionbe.domain.notification.vote.repository;

import com.project.smunionbe.domain.notification.vote.entity.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {
}
