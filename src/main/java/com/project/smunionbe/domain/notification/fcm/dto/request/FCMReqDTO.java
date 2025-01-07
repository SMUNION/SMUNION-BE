package com.project.smunionbe.domain.notification.fcm.dto.request;

import lombok.Builder;

public class FCMReqDTO {

    @Builder
    public record FCMSendDTO(
            String fcmToken,
            String title,
            String body
    ) {
    }

    public record FCMTokenDTO(
            String fcmToken
    ) {
    }
}
