package com.project.smunionbe.domain.club.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "gallery")
public class Gallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    public void update(String name, String thumbnailUrl) {
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
    }
}
