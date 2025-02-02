package com.project.smunionbe.domain.club.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record ClubReqDTO() {
    public record CreateClubDTO(
           @NotBlank(message = "클럽 이름은 필수입니다.")
           String name,

           @NotBlank(message = "클럽 설명은 필수입니다.")
           String description
    ) {
    }

    public record UpdateClubRequest(
            String name,
            String description
    ){
    }

    public record ApproveClubRequest(
            Long departmentId,
            String code,
            String nickname,
            Boolean isStaff
    ){
    }


}

