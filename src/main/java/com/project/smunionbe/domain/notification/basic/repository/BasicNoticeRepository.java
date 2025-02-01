package com.project.smunionbe.domain.notification.basic.repository;

import com.project.smunionbe.domain.notification.basic.entity.BasicNotice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicNoticeRepository extends JpaRepository<BasicNotice, Long> {
}
