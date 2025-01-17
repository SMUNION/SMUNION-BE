package com.project.smunionbe.domain.notification.fee.repository;

import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.notification.fee.entity.FeeNotice;
import com.project.smunionbe.domain.notification.fee.entity.FeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeeStatusRepository extends JpaRepository<FeeStatus, Long> {

    @Query("""
        SELECT mc FROM MemberClub mc
        WHERE mc.club.id = :clubId
        AND mc.id NOT IN (
            SELECT fs.memberClub.id FROM FeeStatus fs WHERE fs.feeNotice.id = :feeId
        )
    """)
    List<MemberClub> findUnpaidMembersByFeeNotice(@Param("feeNotice") FeeNotice feeNotice);
}
