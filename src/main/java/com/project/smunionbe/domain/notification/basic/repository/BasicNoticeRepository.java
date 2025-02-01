package com.project.smunionbe.domain.notification.basic.repository;

import com.project.smunionbe.domain.notification.basic.entity.BasicNotice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BasicNoticeRepository extends JpaRepository<BasicNotice, Long> {

    @Query("""
        SELECT bn FROM BasicNotice bn
        WHERE bn.club.id = :clubId
        AND (:cursor IS NULL OR bn.id < :cursor)
        ORDER BY bn.id DESC
    """)
    List<BasicNotice> findByClubIdAndCursor(
            @Param("clubId") Long clubId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );
}
