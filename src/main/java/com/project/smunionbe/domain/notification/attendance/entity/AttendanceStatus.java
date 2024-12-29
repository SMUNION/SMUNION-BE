package com.project.smunionbe.domain.notification.attendance.entity;

import com.project.smunionbe.domain.member.entity.MemberClub;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "attendance_status")
public class AttendanceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private AttendanceNotice attendanceNotice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_club_id", nullable = false)
    private MemberClub memberClub;

    @Column(name = "is_present")
    private Boolean isPresent;

    @Column(name = "attendance_at")
    private LocalDateTime attendanceAt;

    // 출석 상태 업데이트 메서드
    public void markPresent() {
        this.isPresent = true;
        this.attendanceAt = LocalDateTime.now();
    }
}

