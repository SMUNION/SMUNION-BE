package com.project.smunionbe.domain.notification.vote.repository;

import com.project.smunionbe.domain.notification.vote.entity.VoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VoteStatusRepository extends JpaRepository<VoteStatus, Long> {

    @Query("SELECT COUNT(vs) > 0 " +
            "FROM VoteStatus vs " +
            "WHERE vs.voteNotice.id = :voteNoticeId " +
            "AND vs.memberClub.id = :memberClubId")
    boolean existsByVoteNoticeIdAndMemberClubId(@Param("voteNoticeId") Long voteNoticeId,
                                                @Param("memberClubId") Long memberClubId);

    // 특정 투표 공지의 전체 투표 수
    long countByVoteNoticeId(Long voteNoticeId);

    // 특정 투표 항목에 대한 투표 수
    long countByVoteItemId(Long voteItemId);
}
