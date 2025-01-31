package com.project.smunionbe.domain.notification.attendance.repository;

import com.project.smunionbe.domain.club.entity.Gallery;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<AttendanceNotice, Long> {

    @Query("SELECT a FROM AttendanceNotice a " +
            "WHERE a.club.id = :clubId " +
            "AND (:cursor IS NULL OR a.id < :cursor) " +
            "ORDER BY a.id DESC")
    Slice<AttendanceNotice> findByClubIdAndCursor(
            @Param("clubId") Long clubId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    List<AttendanceNotice> findAllByClubId(Long clubId);

}
