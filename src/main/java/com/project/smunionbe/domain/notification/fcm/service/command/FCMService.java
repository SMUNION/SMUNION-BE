package com.project.smunionbe.domain.notification.fcm.service.command;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.project.smunionbe.domain.notification.fcm.converter.FCMConverter;
import com.project.smunionbe.domain.notification.fcm.dto.request.FCMReqDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FCMService {

    // FCM 푸시 알림 전송 메서드
    public void sendFcmNotification(FCMReqDTO.FCMSendDTO fcmSendDTO) {
        Message message = FCMConverter.toFirebaseMessage(fcmSendDTO);

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("푸시 알림 전송 성공: {}", response);
        } catch (Exception e) {
            log.error("푸시 알림 전송 실패", e);
        }
    }
}
