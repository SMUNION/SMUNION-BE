package com.project.smunionbe.domain.member.repository;

import com.project.smunionbe.domain.member.entity.MemberClub;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberClubRepository extends JpaRepository<MemberClub, Long> {
    boolean existsByMemberIdAndClubId(Long memberId, Long clubId);
}
