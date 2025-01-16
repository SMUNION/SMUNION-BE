package com.project.smunionbe.domain.member.service;

import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ClubSelectionService {

    private static final String SELECTED_PROFILE_KEY = "selectedProfile";
    private final MemberClubRepository memberClubRepository;
    private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 주입

    // 선택된 프로필 저장
    public void setSelectedProfile(HttpSession session, Long memberId, Long memberClubId) {
        // 세션에서 기존 데이터를 가져옴
        Map<Long, Long> selectedProfiles = (Map<Long, Long>) session.getAttribute(SELECTED_PROFILE_KEY);

        if (selectedProfiles == null) {
            selectedProfiles = new HashMap<>();
        }

        // 기존 memberId와 관련된 데이터 갱신
        selectedProfiles.put(memberId, memberClubId);

        // 세션에 저장
        session.setAttribute(SELECTED_PROFILE_KEY, selectedProfiles);

        // Redis에 선택된 프로필 저장 (직렬화하여 저장)
        saveSelectedProfileToRedis(selectedProfiles);
    }

    // Redis에 선택된 프로필 저장
    private void saveSelectedProfileToRedis(Map<Long, Long> selectedProfiles) {
        // Redis에 저장 (직렬화된 Map을 String 형식으로 저장)
        redisTemplate.opsForHash().put(SELECTED_PROFILE_KEY, "selectedProfile", selectedProfiles);
    }

    // 선택된 프로필 조회
    public Long getSelectedProfile(HttpSession session, Long memberId) {
        // Redis에서 선택된 프로필 가져오기
        Map<Long, Long> selectedProfiles = (Map<Long, Long>) redisTemplate.opsForHash().get(SELECTED_PROFILE_KEY, "selectedProfile");
        System.out.println("redis에서 조회");

        if (selectedProfiles == null || !selectedProfiles.containsKey(memberId)) {
            // Redis에 값이 없다면, 세션에서 조회하여 Redis에 저장
            selectedProfiles = (Map<Long, Long>) session.getAttribute(SELECTED_PROFILE_KEY);
            System.out.println("세션에서 조회");

            if (selectedProfiles == null || !selectedProfiles.containsKey(memberId)) {
                // 세션에도 값이 없다면 DB에서 조회하여 세션과 Redis에 저장
                System.out.println("DB에서 조회");
                Pageable pageable = PageRequest.of(0, 1);
                MemberClub memberClub = memberClubRepository.findByMemberId(memberId, pageable)
                        .stream().findFirst()
                        .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));

                setSelectedProfile(session, memberId, memberClub.getId());
                selectedProfiles = (Map<Long, Long>) session.getAttribute(SELECTED_PROFILE_KEY);
            }

            // 세션 데이터를 Redis에 저장
            saveSelectedProfileToRedis(selectedProfiles);
        }

        return selectedProfiles.get(memberId);
    }

}

