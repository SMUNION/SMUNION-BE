package com.project.smunionbe.domain.notification.attendance.entity;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "attendance_notice")
public class AttendanceNotice extends BaseEntity {

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

    public void update(String title, String content, String target, LocalDateTime date) {
        this.title = title;
        this.content = content;
        this.target = target;
        this.date = date;
    }
}
