package com.project.smunionbe.domain.notification.attendance.repository;

import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AttendanceStatusRepository extends JpaRepository<AttendanceStatus, Long> {

    @Query("SELECT s FROM AttendanceStatus s " +
            "WHERE s.attendanceNotice.id = :attendanceId AND s.memberClub.id = :memberClubId")
    Optional<AttendanceStatus> findByAttendanceAndMemberClub(@Param("attendanceId") Long attendanceId, @Param("memberClubId") Long memberClubId);

    // 특정 AttendanceNotice에 연결된 모든 상태 조회
    List<AttendanceStatus> findAllByAttendanceNotice(AttendanceNotice attendanceNotice);

    @Modifying
    @Query("DELETE FROM AttendanceStatus a WHERE a.attendanceNotice.id = :attendanceNoticeId")
    void deleteAllByAttendanceNoticeId(@Param("attendanceNoticeId") Long attendanceNoticeId);

    // 특정 출석 공지와 멤버 클럽의 출석 상태가 존재하는지 확인
    boolean existsByAttendanceNoticeAndMemberClub(AttendanceNotice attendanceNotice, MemberClub memberClub);
}
