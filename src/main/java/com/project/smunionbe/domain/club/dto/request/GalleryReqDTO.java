package com.project.smunionbe.domain.club.dto.request;

public record GalleryReqDTO() {
    public record CreateGalleryDTO(
            String name,
            String thumbnailUrl
    ) {
    }

    public record UpdateGalleryRequest(
            String name,
            String thumbnailUrl
    ){
    }
}
