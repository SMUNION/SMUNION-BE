package com.project.smunionbe.domain.notification.fee.entity;

import com.project.smunionbe.domain.member.entity.MemberClub;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "fee_status")
public class FeeStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_club_id", nullable = false)
    private MemberClub memberClub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_id", nullable = false)
    private FeeNotice feeNotice;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid;

    // 납부 상태 업데이트 메서드
    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }
}

