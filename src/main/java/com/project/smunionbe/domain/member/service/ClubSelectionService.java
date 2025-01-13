package com.project.smunionbe.domain.member.service;

import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import com.project.smunionbe.domain.member.repository.MemberClubRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ClubSelectionService {

    private static final String SELECTED_PROFILE_KEY = "selectedProfile";
    private final MemberClubRepository memberClubRepository;

    public ClubSelectionService(MemberClubRepository memberClubRepository) {
        this.memberClubRepository = memberClubRepository;
    }

    // 선택된 프로필 저장 (memberId와 함께 저장)
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
    }

    // 선택된 프로필 조회
    public Long getSelectedProfile(HttpSession session, Long memberId) {
        Map<Long, Long> selectedProfiles = (Map<Long, Long>) session.getAttribute(SELECTED_PROFILE_KEY);

        //저장되어 있는 값이 없을 시 초기값 설정
        if (selectedProfiles == null || !selectedProfiles.containsKey(memberId)) {
            //throw new MemberClubException(MemberClubErrorCode.SELECTED_NOT_FOUND);
            Pageable pageable = PageRequest.of(0, 1); //1개 값만 가져오기
            MemberClub memberClub = memberClubRepository.findByMemberId(memberId, pageable)
                    .stream().findFirst()
                    .orElseThrow(() -> new MemberClubException(MemberClubErrorCode.MEMBER_CLUB_NOT_FOUND));
            setSelectedProfile(session, memberId, memberClub.getId());
            selectedProfiles = (Map<Long, Long>) session.getAttribute(SELECTED_PROFILE_KEY);
            return selectedProfiles.get(memberId);
        }

        return selectedProfiles.get(memberId);
    }

    // 선택된 프로필 삭제
    public void removeSelectedProfile(HttpSession session, Long memberId) {
        Map<Long, Long> selectedProfiles = (Map<Long, Long>) session.getAttribute(SELECTED_PROFILE_KEY);

        if (selectedProfiles != null) {
            selectedProfiles.remove(memberId);
            // 빈 Map이 되면 세션에서 전체 키 삭제
            if (selectedProfiles.isEmpty()) {
                session.removeAttribute(SELECTED_PROFILE_KEY);
            } else {
                session.setAttribute(SELECTED_PROFILE_KEY, selectedProfiles);
            }
        }
    }
}
