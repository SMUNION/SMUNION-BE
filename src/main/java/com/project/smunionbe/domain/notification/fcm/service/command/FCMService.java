package com.project.smunionbe.domain.notification.fcm.service.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
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

        log.info("FCM 메시지 전송 시도: token={}, title={}, body={}",
                fcmSendDTO.fcmToken(),
                fcmSendDTO.title(),
                fcmSendDTO.body());

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String messageJson = objectMapper.writeValueAsString(message);
            log.info("FCM 메시지 구조: {}", messageJson);
        } catch (Exception e) {
            log.warn("FCM 메시지 구조 로깅 실패", e);
        }

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM 푸시 알림 전송 성공: {}", response);
        } catch (Exception e) {
            log.error("FCM 푸시 알림 전송 실패", e);
            // 예외 상세 정보 로깅
            if (e instanceof FirebaseMessagingException) {
                FirebaseMessagingException fcmException = (FirebaseMessagingException) e;
                log.error("FCM 오류 코드: {}, 오류 메시지: {}",
                        fcmException.getMessagingErrorCode(),
                        fcmException.getMessage());
            }
        }
    }
}
