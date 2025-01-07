package com.project.smunionbe.domain.club.service.command;


import com.project.smunionbe.domain.club.converter.DepartmentConverter;
import com.project.smunionbe.domain.club.converter.GalleryConverter;
import com.project.smunionbe.domain.club.dto.request.DepartmentReqDTO;
import com.project.smunionbe.domain.club.dto.request.GalleryReqDTO;
import com.project.smunionbe.domain.club.dto.response.DepartmentResDTO;
import com.project.smunionbe.domain.club.dto.response.GalleryResDTO;
import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.club.entity.Gallery;
import com.project.smunionbe.domain.club.exception.*;
import com.project.smunionbe.domain.club.repository.ClubRepository;
import com.project.smunionbe.domain.club.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DepartmentCommandService {
    private final ClubRepository clubRepository;
    private final DepartmentRepository departmentRepository;

    public DepartmentResDTO.CreateDepartmentDTO createDepartment(DepartmentReqDTO.CreateDepartmentDTO request) {

        Club club = clubRepository.findById(request.clubId())
                .orElseThrow(() -> new ClubException(ClubErrorCode.CLUB_NOT_FOUND));

        Department department = DepartmentConverter.toDepartment(request, club);
        departmentRepository.save(department);
        return DepartmentConverter.toCreateDepartmentResponse(department);

    }

    public void deleteDepartment(Long departmentId, Long memberId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentException(DepartmentErrorCode.DEPARTMENT_NOT_FOUND));

        if (!clubRepository.existsByid(department.getClub().getId())) {
            throw new ClubException(ClubErrorCode.CLUB_NOT_FOUND);
        }
        /*if (!memberClubRepository.existsByMemberIdAndClubId(memberId, gallery.getClub().getId())) {
            throw new ClubException(ClubErrorCode.ACCESS_DENIED);
        }*/

        departmentRepository.delete(department);
    }
}
