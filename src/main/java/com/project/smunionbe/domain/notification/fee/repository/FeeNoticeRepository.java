package com.project.smunionbe.domain.notification.fee.repository;

import com.project.smunionbe.domain.notification.fee.entity.FeeNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeNoticeRepository extends JpaRepository<FeeNotice, Long> {
}
