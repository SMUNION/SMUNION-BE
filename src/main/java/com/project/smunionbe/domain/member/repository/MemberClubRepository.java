package com.project.smunionbe.domain.member.repository;

import com.project.smunionbe.domain.member.entity.MemberClub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberClubRepository extends JpaRepository<MemberClub, Long> {

    // 특정 부서 대상 멤버 조회
    @Query("SELECT mc FROM MemberClub mc WHERE mc.club.id = :clubId AND mc.department.name IN :targetDepartments")
    List<MemberClub> findAllByClubIdAndDepartments(@Param("clubId") Long clubId, @Param("targetDepartments") List<String> targetDepartments);

    @Query("SELECT mc.id FROM MemberClub mc WHERE mc.member.id = :memberId AND mc.club.id = :clubId")
    Optional<Long> findIdByMemberIdAndClubId(@Param("memberId") Long memberId, @Param("clubId") Long clubId);

    // 전체 부서 멤버 조회
    List<MemberClub> findAllByClubId(Long clubId);

    boolean existsByMemberIdAndClubId(Long memberId, Long clubId);
}
