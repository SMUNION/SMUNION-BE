package com.project.smunionbe.domain.notification.vote.repository;

import com.project.smunionbe.domain.notification.vote.entity.VoteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteItemRepository extends JpaRepository<VoteItem, Long> {

    List<VoteItem> findAllByVoteNoticeId(Long voteNoticeId);

    @Modifying
    @Query("DELETE FROM VoteItem vi WHERE vi.voteNotice.id = :voteNoticeId")
    void deleteAllByVoteNoticeId(@Param("voteNoticeId") Long voteNoticeId);
}
