package com.project.smunionbe.domain.club.dto.response;

import com.project.smunionbe.domain.club.entity.Department;

import java.util.List;

public record DepartmentResDTO() {

    public record CreateDepartmentDTO(
            Long departmentId,
            String clubName,
            String name
    ) {
    }

    public record GetDepartmentListResDTO(
            List<DepartmentResDTO.CreateDepartmentDTO> departmentDTOS
    ){
    }
}
