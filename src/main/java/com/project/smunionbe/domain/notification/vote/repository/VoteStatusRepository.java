package com.project.smunionbe.domain.notification.vote.repository;

import com.project.smunionbe.domain.notification.vote.entity.VoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteStatusRepository extends JpaRepository<VoteStatus, Long> {

    @Query("SELECT COUNT(vs) > 0 " +
            "FROM VoteStatus vs " +
            "WHERE vs.voteNotice.id = :voteNoticeId " +
            "AND vs.memberClub.id = :memberClubId")
    boolean existsByVoteNoticeIdAndMemberClubId(@Param("voteNoticeId") Long voteNoticeId,
                                                @Param("memberClubId") Long memberClubId);

    @Query("SELECT vs FROM VoteStatus vs " +
            "WHERE vs.voteNotice.id = :voteNoticeId " +
            "AND vs.voteItem.id = :voteItemId " +
            "AND vs.memberClub.id = :memberClubId")
    Optional<VoteStatus> findByVoteNoticeIdAndVoteItemIdAndMemberClubId(
            @Param("voteNoticeId") Long voteNoticeId,
            @Param("voteItemId") Long voteItemId,
            @Param("memberClubId") Long memberClubId
    );

    @Query("SELECT vs FROM VoteStatus vs WHERE vs.voteNotice.id = :voteNoticeId")
    List<VoteStatus> findByVoteNoticeId(@Param("voteNoticeId") Long voteNoticeId);


    @Modifying
    @Query("DELETE FROM VoteStatus vs WHERE vs.voteNotice.id = :voteNoticeId")
    void deleteAllByVoteNoticeId(@Param("voteNoticeId") Long voteNoticeId);
}
