package com.project.smunionbe.domain.club.repository;

import com.project.smunionbe.domain.club.entity.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {

    // 사용자 ID와 동아리 ID를 함께 검증
    Optional<Club> findByIdAndUserId(Long clubId, Long userId);
}
