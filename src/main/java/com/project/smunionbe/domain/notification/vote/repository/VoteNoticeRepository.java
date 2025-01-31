package com.project.smunionbe.domain.notification.vote.repository;

import com.project.smunionbe.domain.club.entity.Gallery;
import com.project.smunionbe.domain.notification.vote.entity.VoteNotice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteNoticeRepository extends JpaRepository<VoteNotice, Long> {

    @Query("SELECT vn FROM VoteNotice vn " +
            "WHERE vn.club.id = :clubId " +
            "AND (:cursor IS NULL OR vn.id < :cursor) " +
            "ORDER BY vn.id DESC")
    Slice<VoteNotice> findByClubIdAndCursor(
            @Param("clubId") Long clubId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );
    List<VoteNotice> findAllByClubId(Long clubId);


}
