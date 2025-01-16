package com.project.smunionbe.domain.notification.vote.converter;

import com.project.smunionbe.domain.club.entity.Club;
import com.project.smunionbe.domain.notification.fcm.dto.request.FCMReqDTO;
import com.project.smunionbe.domain.notification.vote.dto.request.VoteReqDTO;
import com.project.smunionbe.domain.notification.vote.entity.VoteItem;
import com.project.smunionbe.domain.notification.vote.entity.VoteNotice;

import java.util.List;
import java.util.stream.Collectors;

public class VoteConverter {

    public static VoteNotice toVoteNotice(VoteReqDTO.CreateVoteDTO request, Club club) {
        // 타겟 부서 설정: null 또는 빈 값일 경우 "전체"
        String target = (request.targetDepartments() == null || request.targetDepartments().isEmpty())
                ? "전체"
                : String.join(", ", request.targetDepartments()); // 부서 이름들을 콤마로 합치기

        return VoteNotice.builder()
                .club(club)
                .title(request.title())
                .content(request.description())
                .date(request.date())
                .allowDuplicate(request.allowDuplicate())
                .anonymous(request.anonymous())
                .target(target)
                .build();
    }

    public static List<VoteItem> toVoteItems(List<String> options, VoteNotice voteNotice) {
        return options.stream()
                .map(option -> VoteItem.builder()
                        .name(option)
                        .voteNotice(voteNotice)
                        .build())
                .collect(Collectors.toList());
    }

    public static FCMReqDTO.FCMSendDTO toSendDTO(String fcmToken, VoteNotice voteNotice) {
        return FCMReqDTO.FCMSendDTO.builder()
                .fcmToken(fcmToken)
                .title("투표 공지: " + voteNotice.getTitle())
                .body("새로운 투표 공지가 도착했습니다: " + voteNotice.getContent())
                .build();
    }
}
