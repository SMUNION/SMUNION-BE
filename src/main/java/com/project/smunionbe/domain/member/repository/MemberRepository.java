package com.project.smunionbe.domain.member.repository;

import com.project.smunionbe.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT mc.member FROM MemberClub mc " +
            "WHERE mc.club.id = :clubId " +
            "AND mc.id NOT IN (" +
            "    SELECT a.memberClub.id FROM AttendanceStatus a " +
            "    WHERE a.attendanceNotice.id = :attendanceId AND a.isPresent = true" +
            ")")
    List<Member> findAbsenteesByAttendanceId(@Param("attendanceId") Long attendanceId, @Param("clubId") Long clubId);

    boolean existsByEmail(String email);

    Optional<Member> findByEmail(String email); //이메일로 사용자의 정보를 가져옴
}
