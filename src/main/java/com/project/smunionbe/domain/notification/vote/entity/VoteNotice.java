package com.project.smunionbe.domain.notification.vote.entity;

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
@Table(name = "vote_notice")
public class VoteNotice extends BaseEntity {

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

    @Column(name = "allow_duplicate")
    private boolean allowDuplicate;

    @Column(name = "anonymous")
    private boolean anonymous;
}
