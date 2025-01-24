package com.project.smunionbe.domain.notification.fcm.service.event;

import com.project.smunionbe.domain.member.entity.MemberClub;
import com.project.smunionbe.domain.notification.attendance.converter.AttendanceConverter;
import com.project.smunionbe.domain.notification.attendance.entity.AttendanceNotice;
import com.project.smunionbe.domain.notification.fcm.dto.request.FCMReqDTO;
import com.project.smunionbe.domain.notification.fcm.service.command.FCMService;
import com.project.smunionbe.domain.notification.fcm.service.command.FCMTokenService;
import com.project.smunionbe.domain.notification.fee.converter.FeeNoticeConverter;
import com.project.smunionbe.domain.notification.fee.entity.FeeNotice;
import com.project.smunionbe.domain.notification.vote.converter.VoteConverter;
import com.project.smunionbe.domain.notification.vote.entity.VoteNotice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMNotificationService {

    private final FCMService fcmService;
    private final FCMTokenService fcmTokenService;

    /**
     * 출석 공지 알림 전송
     */
    public void sendPushNotifications(AttendanceNotice attendanceNotice, List<MemberClub> targetMembers) {
        for (MemberClub member : targetMembers) {
            // Redis에서 FCM 토큰 조회
            String fcmToken = fcmTokenService.getFcmToken(member.getMember().getEmail());

            // FCM 토큰이 없으면 푸시 알림 건너뛰기
            if (fcmToken == null || fcmToken.isEmpty()) {
                log.warn("사용자 {}의 FCM 토큰이 존재하지 않아 푸시 알림을 건너뜁니다.", member.getMember().getEmail());
                continue;
            }

            // FCM 알림 생성
            FCMReqDTO.FCMSendDTO fcmSendDTO = AttendanceConverter.toSendDTO(fcmToken, attendanceNotice);

            // FCM 푸시 알림 전송
            try {
                fcmService.sendFcmNotification(fcmSendDTO);
                log.info("사용자 {}에게 푸시 알림 전송 성공", member.getMember().getEmail());
            } catch (Exception e) {
                log.error("사용자 {}에게 푸시 알림 전송 실패: {}", member.getMember().getEmail(), e.getMessage());
            }
        }
    }

    /**
     * 투표 공지 알림 전송
     */
    public void sendVotePushNotifications(VoteNotice voteNotice, List<MemberClub> targetMembers) {
        for (MemberClub member : targetMembers) {
            String fcmToken = fcmTokenService.getFcmToken(member.getMember().getEmail());

            if (fcmToken == null || fcmToken.isEmpty()) {
                log.warn("사용자 {}의 FCM 토큰이 존재하지 않아 푸시 알림을 건너뜁니다.", member.getMember().getEmail());
                continue;
            }

            FCMReqDTO.FCMSendDTO fcmSendDTO = VoteConverter.toSendDTO(fcmToken, voteNotice);

            try {
                fcmService.sendFcmNotification(fcmSendDTO);
                log.info("사용자 {}에게 푸시 알림 전송 성공", member.getMember().getEmail());
            } catch (Exception e) {
                log.error("사용자 {}에게 푸시 알림 전송 실패: {}", member.getMember().getEmail(), e.getMessage());
            }
        }
    }

    /**
     * 회비 공지 알림 전송
     */
    public void sendFeePushNotifications(FeeNotice feeNotice, List<MemberClub> targetMembers) {
        for (MemberClub member : targetMembers) {
            // 1. FCM 토큰 조회
            String fcmToken = fcmTokenService.getFcmToken(member.getMember().getEmail());

            // FCM 토큰이 없는 경우 건너뜀
            if (fcmToken == null || fcmToken.isEmpty()) {
                log.warn("사용자 {}의 FCM 토큰이 존재하지 않아 푸시 알림을 건너뜁니다.", member.getMember().getEmail());
                continue;
            }

            // 2. FCM 알림 생성
            FCMReqDTO.FCMSendDTO fcmSendDTO = FeeNoticeConverter.toSendDTO(fcmToken, feeNotice);

            // 3. FCM 푸시 알림 전송
            try {
                fcmService.sendFcmNotification(fcmSendDTO);
                log.info("사용자 {}에게 푸시 알림 전송 성공", member.getMember().getEmail());
            } catch (Exception e) {
                log.error("사용자 {}에게 푸시 알림 전송 실패: {}", member.getMember().getEmail(), e.getMessage());
            }
        }
    }
}
