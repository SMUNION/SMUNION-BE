package com.project.smunionbe.domain.member.entity;

import com.project.smunionbe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "major", nullable = false)
    private String major;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "member", orphanRemoval = true)
    private List<MemberClub> memberClubs;


    //soft delete 수행 메서드
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    //탈퇴한 회원인지 확인
    public boolean isDeleted() {
        return deletedAt != null;
    }

    //비밀번호 변경
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    // 프로필 이미지 업데이트 메서드 추가
    public void updateProfileImage(String profileImageUrl) {
        this.profileImage = profileImageUrl;
    }
}

