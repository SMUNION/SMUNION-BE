package com.project.smunionbe.domain.club.repository;

import com.project.smunionbe.domain.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {

    // 사용자 ID와 동아리 ID를 함께 검증
    @Query("SELECT c FROM Club c JOIN c.memberClubs mc WHERE c.id = :clubId AND mc.member.id = :memberId")
    Optional<Club> findByIdAndMemberId(@Param("clubId") Long clubId, @Param("memberId") Long memberId);

    boolean existsByid(Long Id);

    boolean existsByName(String name);

}
