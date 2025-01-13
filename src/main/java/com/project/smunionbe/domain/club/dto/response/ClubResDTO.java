package com.project.smunionbe.domain.club.dto.response;

import java.util.List;

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

    public record MemberClubResponse(
            Long memberId,
            String nickname,
            String department

    ){
    }

    public record GetMemberClubListResDTO(
            List<ClubResDTO.MemberClubResponse> memberClubResponseList
    ){
    }
}
