package com.project.smunionbe.domain.notification.fee.entity;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "fee_notice")
public class FeeNotice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "target", nullable = false)
    private String target;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "bank", nullable = false)
    private String bank;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "participant_count")
    private int participantCount;
}

