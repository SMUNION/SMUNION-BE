package com.project.smunionbe.domain.notification.fee.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class FeeResDTO {

    @Builder
    public record FeeNoticeListResponse(
            List<FeeNoticeResponse> fees,
            boolean hasNext,
            Long cursor
    ) {
    }

    @Builder
    public record FeeNoticeResponse(
            Long feeId,
            String title,
            String content,
            String target,
            int amount,
            String bank,
            String accountNumber,
            int participantCount,
            LocalDateTime deadline,
            LocalDateTime createdAt
    ) {
    }

    public record UnpaidMembersResponse(
            Long feeId,
            List<UnpaidMemberResponse> unpaidMembers
    ) {
    }

    public record UnpaidMemberResponse(
            Long memberId,
            String nickname
    ) {
    }
}
