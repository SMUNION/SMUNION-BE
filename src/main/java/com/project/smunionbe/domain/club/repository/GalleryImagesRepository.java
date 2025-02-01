package com.project.smunionbe.domain.club.repository;

import com.project.smunionbe.domain.club.entity.Gallery;
import com.project.smunionbe.domain.club.entity.GalleryImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GalleryImagesRepository extends JpaRepository<GalleryImages, Long> {
    @Query("SELECT ai.imageUrl FROM GalleryImages ai WHERE ai.gallery.id = :galleryId")
    List<String> findImageUrlsByGalleryId(@Param("galleryId") Long galleryId);

    @Transactional
    void deleteByGalleryId(Long galleryId);
}
