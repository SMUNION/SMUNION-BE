package com.project.smunionbe.domain.notification.fee.dto.request;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class FeeReqDTO {

    @Builder
    public record CreateFeeNoticeRequestDTO(
            String title,
            String content,
            Integer amount,
            String bank,
            String accountNumber,
            LocalDateTime deadLine,
            Integer participantCount,
            List<String> targetDepartments
    ){
    }

    @Builder
    public record UpdateFeeNoticeRequest(
            String title,
            String content,
            Integer amount,
            String bank,
            String accountNumber,
            LocalDateTime date,
            Integer participantCount,
            List<String> targetDepartments
    ) {
    }
}
