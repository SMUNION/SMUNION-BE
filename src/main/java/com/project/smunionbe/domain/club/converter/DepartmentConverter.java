package com.project.smunionbe.domain.club.converter;

import com.project.smunionbe.domain.club.dto.request.DepartmentReqDTO;
import com.project.smunionbe.domain.club.dto.request.GalleryReqDTO;
import com.project.smunionbe.domain.club.dto.response.DepartmentResDTO;
import com.project.smunionbe.domain.club.dto.response.GalleryResDTO;
import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.club.entity.Gallery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DepartmentConverter {
    public static Department toDepartment(DepartmentReqDTO.CreateDepartmentDTO createDepartmentDTO, Club club) {

        return Department.builder()
                .club(club)
                .name(createDepartmentDTO.name())
                .build();
    }

    public static DepartmentResDTO.CreateDepartmentDTO toCreateDepartmentResponse(Department department) {
        return new DepartmentResDTO.CreateDepartmentDTO(
                department.getId(), department.getClub().getName(), department.getName()
        );
    }
}
