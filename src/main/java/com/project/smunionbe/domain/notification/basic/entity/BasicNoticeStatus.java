package com.project.smunionbe.domain.notification.basic.entity;

import com.project.smunionbe.domain.member.entity.MemberClub;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "basic_notice_status")
public class BasicNoticeStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "basic_notice_id", nullable = false)
    private BasicNotice basicNotice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_club_id", nullable = false)
    private MemberClub memberClub;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    // 읽음 상태 업데이트 메서드
    public void markAsRead() {
        this.isRead = true;
    }
}
