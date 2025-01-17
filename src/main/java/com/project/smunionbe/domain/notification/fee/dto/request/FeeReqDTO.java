package com.project.smunionbe.domain.notification.fee.dto.request;

import lombok.Builder;

import java.time.LocalDateTime;

public class FeeReqDTO {

    @Builder
    public record CreateFeeNoticeRequestDTO(
            Long clubId,
            String title,
            String content,
            Integer amount,
            String bank,
            String accountNumber,
            LocalDateTime deadLine,
            Integer participantCount
    ){
    }
}
