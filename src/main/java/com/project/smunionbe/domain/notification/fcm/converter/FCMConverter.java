package com.project.smunionbe.domain.notification.fcm.converter;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.project.smunionbe.domain.notification.fcm.dto.request.FCMReqDTO;

public class FCMConverter {

    // FcmSendDto를 기반으로 Firebase Message 객체를 생성하는 메서드
    public static Message toFirebaseMessage(FCMReqDTO.FCMSendDTO fcmSendDTO) {
        // 알림 정보 설정
        Notification notification = Notification.builder()
                .setTitle(fcmSendDTO.title())
                .setBody(fcmSendDTO.body())
                .build();

        // 메시지 빌드 (토큰과 알림 내용 포함)
        return Message.builder()
                .setToken(fcmSendDTO.fcmToken())
                .setNotification(notification)
                .build();
    }
}
