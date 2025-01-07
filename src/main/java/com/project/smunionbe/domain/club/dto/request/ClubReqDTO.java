package com.project.smunionbe.domain.club.dto.request;

public record ClubReqDTO() {
    public record CreateClubDTO(
           String name,
           String description,
           String thumbnailUrl
    ) {
    }

    public record UpdateClubRequest(
            String name,
            String description,
            String thumbnailUrl
    ){
    }


}

