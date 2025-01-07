package com.project.smunionbe.domain.club.service.query;

import com.project.smunionbe.domain.club.converter.GalleryConverter;
import com.project.smunionbe.domain.club.dto.response.DepartmentResDTO;
import com.project.smunionbe.domain.club.dto.response.GalleryResDTO;
import com.project.smunionbe.domain.club.entity.Gallery;
import com.project.smunionbe.domain.club.exception.GalleryErrorCode;
import com.project.smunionbe.domain.club.exception.GalleryException;
import com.project.smunionbe.domain.club.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentQueryService {
    private final DepartmentRepository departmentRepository;
    public DepartmentResDTO.GetDepartmentListResDTO getAllDepartment(
            Long clubId
    ) {


        // 데이터 변환
        List<DepartmentResDTO.CreateDepartmentDTO> departmentResDTOS = departmentRepository.findAllByClubId(clubId)
                .stream()
                .map(department -> new DepartmentResDTO.CreateDepartmentDTO(
                        department.getId(),
                        department.getClub().getName(),
                        department.getName()
                ))
                .toList();

        return new DepartmentResDTO.GetDepartmentListResDTO(departmentResDTOS);
    }
}
