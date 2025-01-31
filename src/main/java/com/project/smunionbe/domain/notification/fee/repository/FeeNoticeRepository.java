package com.project.smunionbe.domain.notification.fee.repository;

import com.project.smunionbe.domain.club.entity.Gallery;
import com.project.smunionbe.domain.notification.fee.entity.FeeNotice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeeNoticeRepository extends JpaRepository<FeeNotice, Long> {

    @Query("""
        SELECT f FROM FeeNotice f
        WHERE f.club.id = :clubId
        AND (:cursor IS NULL OR f.id < :cursor)
        ORDER BY f.createdAt DESC
    """)
    List<FeeNotice> findByClubIdWithCursor(
            @Param("clubId") Long clubId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    List<FeeNotice> findAllByClubId(Long clubId);

}
