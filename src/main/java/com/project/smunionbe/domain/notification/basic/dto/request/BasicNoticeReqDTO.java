package com.project.smunionbe.domain.notification.basic.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public class BasicNoticeReqDTO {

    public record CreateBasicNoticeRequest(
            String title,
            String content,
            List<String> targetDepartments,
            LocalDateTime date
    ) {
    }

    public record UpdateBasicNoticeRequest(
            String title,
            String content,
            List<String> targetDepartments,
            LocalDateTime date
    ) {
    }
}
