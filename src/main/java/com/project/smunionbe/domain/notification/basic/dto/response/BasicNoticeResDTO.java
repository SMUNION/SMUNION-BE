package com.project.smunionbe.domain.notification.basic.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class BasicNoticeResDTO {

    /**
     * 일반 공지 목록 응답 DTO
     */
    public record BasicNoticeListResponse(
            List<BasicNoticeResponse> notices,
            boolean hasNext,
            Long cursor
    ) {
    }

    /**
     * 일반 공지 개별 응답 DTO (목록, 상세 공용)
     */
    public record BasicNoticeResponse(
            Long noticeId,
            String title,
            String content,
            String target,
            LocalDateTime date,
            LocalDateTime createdAt
    ) {
    }

    /**
     * 미확인 인원 응답 DTO
     */
    public record UnreadMembersResponse(
            Long noticeId,
            List<UnreadMemberResponse> unreadMembers
    ) {
    }

    /**
     * 미확인 멤버 개별 응답 DTO
     */
    public record UnreadMemberResponse(
            Long memberId,
            String nickname
    ) {
    }

    /**
     * 일반 공지 상세 응답 DTO
     */
    public record BasicNoticeDetailResponse(
            Long noticeId,
            String title,
            String content,
            String target,
            LocalDateTime date,
            LocalDateTime createdAt,
            String clubName // 동아리 정보 포함
    ) {
    }
}
