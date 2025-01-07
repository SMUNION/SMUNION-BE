package com.project.smunionbe.domain.club.repository;

import com.project.smunionbe.domain.club.entity.Department;
import com.project.smunionbe.domain.club.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findAllByClubId(Long clubId);

}
