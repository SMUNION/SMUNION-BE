package com.project.smunionbe.domain.member.security;

import com.project.smunionbe.domain.member.entity.Member;
import com.project.smunionbe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
//스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    //사용자 이메일로 사용자의 정보를 가져오는 메서드
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("이메일에 해당하는 유저를 찾을 수 없습니다.: " + email));
        return new CustomUserDetails(member);
    }
}

