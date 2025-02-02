package com.project.smunionbe.domain.club.dto.request;

public record GalleryReqDTO() {
    public record CreateGalleryDTO(
            String name
    ) {
    }

    public record UpdateGalleryRequest(
            String name

    ){
    }
}
