package com.project.smunionbe.domain.member.security;


import com.project.smunionbe.domain.member.entity.Member;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER")); // 현재 권한은 기본적으로 ROLE_USER
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 로직을 필요에 따라 추가 가능
    }

    @Override
    public boolean isAccountNonLocked() {
        return !member.isDeleted(); //탈퇴한 계정은 로그인 차단
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부 로직 추가 가능
    }

    @Override
    public boolean isEnabled() {
        return !member.isDeleted(); //  탈퇴한 계정은 활성화 X
    }
}

