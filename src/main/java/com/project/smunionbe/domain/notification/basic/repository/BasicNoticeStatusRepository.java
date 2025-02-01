package com.project.smunionbe.domain.notification.basic.repository;

import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.notification.basic.entity.BasicNotice;
import com.project.smunionbe.domain.notification.basic.entity.BasicNoticeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BasicNoticeStatusRepository extends JpaRepository<BasicNoticeStatus, Long> {

    @Query("""
        SELECT mc FROM BasicNoticeStatus bns JOIN bns.memberClub mc
        WHERE bns.basicNotice = :basicNotice AND bns.isRead = false
    """)
    List<MemberClub> findUnreadMembersByNotice(@Param("basicNotice") BasicNotice basicNotice);

    @Query("""
        SELECT bns FROM BasicNoticeStatus bns 
        WHERE bns.basicNotice = :notice 
        AND bns.memberClub = :memberClub
    """)
    Optional<BasicNoticeStatus> findByNoticeAndMemberClub(
            @Param("notice") BasicNotice notice,
            @Param("memberClub") MemberClub memberClub
    );
}
