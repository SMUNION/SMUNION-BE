package com.project.smunionbe.domain.club.dto.response;

import com.project.smunionbe.domain.notification.attendance.dto.response.AttendanceResDTO;

import java.util.List;

public class GalleryResDTO {

    public record CreateGalleryResDTO(
            Long galleryID
    ) {
    }

    public record UpdateGalleryResDTO(
            String name,
            String thumbnailUrl
    ){
    }

    public record GetGalleryResDTO(
            Long galleryID,
            String name,
            List<String> thumbnailImages
            ){
    }

    public record GetGalleryListResDTO(
            List<GalleryResDTO.GetGalleryResDTO> galleryResDTOS
    ){
    }
}
