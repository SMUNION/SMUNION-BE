package com.project.smunionbe.domain.club.repository;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    List<Gallery> findAllByClubId(Long clubId);

}
