package com.project.smunionbe.domain.notification.attendance.repository;

import com.project.smunionbe.domain.notification.attendance.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AttendanceStatusRepository extends JpaRepository<AttendanceStatus, Long> {

    @Query("SELECT s FROM AttendanceStatus s " +
            "WHERE s.attendanceNotice.id = :attendanceId AND s.memberClub.id = :memberClubId")
    Optional<AttendanceStatus> findByAttendanceAndMemberClub(@Param("attendanceId") Long attendanceId, @Param("memberClubId") Long memberClubId);
}
