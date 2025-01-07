package com.project.smunionbe.domain.club.dto.request;

public record DepartmentReqDTO() {
    public record CreateDepartmentDTO(
            String name,
            Long clubId
    ) {
    }

}
