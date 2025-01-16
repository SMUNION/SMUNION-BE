package com.project.smunionbe.domain.notification.vote.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public class VoteReqDTO {

    public record CreateVoteDTO(
            String title, // 투표 제목
            String description, // 투표 내용
            List<String> targetDepartments, // 특정 부서들
            LocalDateTime date, // 투표 마감일
            boolean allowDuplicate, // 중복 가능 여부
            boolean anonymous, // 익명 여부
            List<String> options // 투표 항목 리스트
    ) {
    }

    public record ParticipateVoteDTO(
            List<Long> voteOptionIds // 선택한 항목 ID 리스트
    ) {
    }

    public record UpdateVoteDTO(
            String title, // 수정된 제목
            String content, // 수정된 내용
            LocalDateTime date, // 수정된 마감일
            boolean allowDuplicate, // 중복 투표 허용 여부
            List<String> options // 수정된 투표 항목 리스트
    ) {
    }
}
