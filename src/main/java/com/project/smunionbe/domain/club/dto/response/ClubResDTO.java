package com.project.smunionbe.domain.club.dto.response;

public record ClubResDTO() {

    public record CreateClubDTO(
            Long clubID
    ) {
    }

    public record UpdateClubRequest(
            String name,
            String description,
            String thumbnailUrl
    ){
    }
}
