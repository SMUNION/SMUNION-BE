package com.project.smunionbe.domain.club.converter;

import com.project.smunionbe.domain.club.entity.Gallery;
import com.project.smunionbe.domain.club.entity.GalleryImages;
import com.project.smunionbe.domain.community.entity.Article;
import com.project.smunionbe.domain.community.entity.ArticleImages;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class GalleryImagesConverter {
    public GalleryImages toGalleryImages(Gallery gallery, String image) {
        return GalleryImages.builder()
                .gallery(gallery)
                .imageUrl(image)
                .build();
    }
}
