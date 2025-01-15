package com.project.smunionbe.domain.community.entity;

import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "article")
public class Article extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_club_id", nullable = false)
    private MemberClub memberClub;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "department", nullable = false)
    private String department;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @Column(name = "like_num", nullable = false)
    @Builder.Default
    private Integer LikeNum = 0;
}