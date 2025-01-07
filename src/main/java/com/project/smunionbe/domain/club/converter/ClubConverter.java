package com.project.smunionbe.domain.club.converter;


import com.project.smunionbe.domain.club.dto.request.ClubReqDTO;
import com.project.smunionbe.domain.club.dto.response.ClubResDTO;
import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.notification.attendance.dto.request.AttendanceReqDTO;
import com.project.smunionbe.domain.notification.attendance.dto.response.AttendanceResDTO;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClubConverter {

    public static Club toClub(ClubReqDTO.CreateClubDTO createClubDTO) {

        return Club.builder()
                .name(createClubDTO.name())
                .description(createClubDTO.description())
                .thumbnailUrl(createClubDTO.thumbnailUrl())
                .build();
    }

    public static ClubResDTO.CreateClubDTO toClubResponse(Club club) {
        return new ClubResDTO.CreateClubDTO(
               club.getId()
        );
    }

}
