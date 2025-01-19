package com.project.smunionbe.domain.notification.fee.repository;

import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.notification.fee.entity.FeeNotice;
import com.project.smunionbe.domain.notification.fee.entity.FeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeeStatusRepository extends JpaRepository<FeeStatus, Long> {

    @Query("""
    SELECT mc FROM FeeStatus fs JOIN fs.memberClub mc
    WHERE fs.feeNotice.id = :feeId AND fs.isPaid = false
""")
    List<MemberClub> findUnpaidMembersByFeeNotice(@Param("feeId") Long feeId);


    Optional<FeeStatus> findByFeeNoticeAndMemberClub(FeeNotice feeNotice, MemberClub memberClub);

    // 특정 FeeNotice에 연결된 모든 FeeStatus 조회
    List<FeeStatus> findAllByFeeNotice(FeeNotice feeNotice);

    @Modifying
    @Query("DELETE FROM FeeStatus fs WHERE fs.feeNotice.id = :feeNoticeId")
    void deleteAllByFeeNoticeId(@Param("feeNoticeId") Long feeNoticeId);
}
