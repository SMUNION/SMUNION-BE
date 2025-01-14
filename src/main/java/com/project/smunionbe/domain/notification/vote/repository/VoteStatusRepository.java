package com.project.smunionbe.domain.notification.vote.repository;

import com.project.smunionbe.domain.notification.vote.entity.VoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteStatusRepository extends JpaRepository<VoteStatus, Long> {

    // 특정 멤버가 특정 투표 항목에 대해 이미 투표했는지 확인
    Optional<VoteStatus> findByVoteNoticeIdAndMemberClubId(Long voteNoticeId, Long memberClubId);
}
