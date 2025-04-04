package com.project.smunionbe.domain.notification.vote.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public class VoteResDTO {

    public record VoteResponse(
            Long voteId,
            String title,
            String content,
            String target,
            LocalDateTime date,
            boolean allowDuplicate,
            boolean anonymous,
            LocalDateTime createdAt
    ) {
    }

    public record VoteListResponse(
            List<VoteResponse> votes,
            boolean hasNext,
            Long cursor
    ) {
    }

    public record VoteOptionResponse(
            Long voteOptionId,
            String optionName
    ) {
    }

    public record VoteDetailResponse(
            Long voteId,
            String title,
            String content,
            String target,
            LocalDateTime date,
            boolean allowDuplicate,
            boolean anonymous,
            List<VoteOptionResponse> options,
            LocalDateTime createdAt
    ) {
    }

    public record VoteAbsenteesResponse(
            List<Absentee> absentees
    ) {}

    public record Absentee(
            Long memberId,
            String nickname
    ) {}

    public record VoteResultResponse(
            List<VoteResult> results,
            boolean anonymous // 익명 여부 추가
    ) {
    }

    public record VoteResult(
            Long voteOptionId,
            String optionName,
            Long votes,
            Integer percentage
    ) {
    }
}
