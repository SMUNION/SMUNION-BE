package com.project.smunionbe.domain.notification.vote.repository;

import com.project.smunionbe.domain.notification.vote.entity.VoteNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteNoticeRepository extends JpaRepository<VoteNotice, Long> {
}
