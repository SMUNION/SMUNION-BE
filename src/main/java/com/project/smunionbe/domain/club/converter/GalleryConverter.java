package com.project.smunionbe.domain.club.converter;

import com.project.smunionbe.domain.club.dto.request.ClubReqDTO;
import com.project.smunionbe.domain.club.dto.request.GalleryReqDTO;
import com.project.smunionbe.domain.club.dto.response.ClubResDTO;
import com.project.smunionbe.domain.club.dto.response.GalleryResDTO;
import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Gallery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GalleryConverter {
    public static Gallery toGallery(GalleryReqDTO.CreateGalleryDTO createGalleryDTO,Club club) {

        return Gallery.builder()
                .club(club)
                .name(createGalleryDTO.name())
                .thumbnailUrl(createGalleryDTO.thumbnailUrl())
                .build();
    }

    public static GalleryResDTO.CreateGalleryResDTO toCreateGalleryResponse(Gallery gallery) {
        return new GalleryResDTO.CreateGalleryResDTO(
                gallery.getId()
        );
    }

    public static GalleryResDTO.GetGalleryResDTO toGetGalleryResponse(Gallery gallery) {
        return new GalleryResDTO.GetGalleryResDTO(
                gallery.getId(),
                gallery.getName(),
                gallery.getThumbnailUrl()
        );
    }


}
