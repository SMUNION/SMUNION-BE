package com.project.smunionbe.domain.member.service;

import com.project.smunionbe.domain.member.exception.MemberClubErrorCode;
import com.project.smunionbe.domain.member.exception.MemberClubException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ClubSelectionService {

    private static final String SELECTED_PROFILE_KEY = "selectedProfile";

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

        if (selectedProfiles == null || !selectedProfiles.containsKey(memberId)) {
            throw new MemberClubException(MemberClubErrorCode.SELECTED_NOT_FOUND);
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
