package com.project.smunionbe.domain.notification.attendance.repository;

import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<AttendanceNotice, Long> {
}
