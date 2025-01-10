package com.project.smunionbe.domain.notification.fcm.controller;

import com.project.smunionbe.domain.member.security.CustomUserDetails;
import com.project.smunionbe.domain.notification.fcm.dto.request.FCMReqDTO;
import com.project.smunionbe.domain.notification.fcm.service.command.FCMService;
import com.project.smunionbe.domain.notification.fcm.service.command.FCMTokenService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
@Tag(name = "푸시 알림 관련 API", description = "푸시 알림 관련 API입니다.")
public class FCMController {

    private final FCMTokenService fcmTokenService;
    private final FCMService fcmService;

    // FCM 토큰 저장 API
    @Operation(summary = "FCM 토큰 저장", description = "로그인된 사용자의 FCM 토큰을 저장합니다.")
    @PostMapping("/token")
    public CustomResponse<String> registerFcmToken(@RequestBody FCMReqDTO.FCMTokenDTO fcmTokenDTO,
                                                   @AuthenticationPrincipal CustomUserDetails authMember) {
        fcmTokenService.saveFcmToken(authMember.getUsername(), fcmTokenDTO.fcmToken());
        return CustomResponse.onSuccess("성공적으로 FCM 토큰이 저장되었습니다.");
    }

    // FCM 푸시 알림 전송 API
    @Operation(summary = "테스트 FCM 푸시 알림 전송", description = "지정된 FCM 토큰으로 푸시 알림을 전송합니다.")
    @PostMapping("/send")
    public CustomResponse<String> sendNotification(@RequestBody FCMReqDTO.FCMSendDTO fcmSendDTO) {
        fcmService.sendFcmNotification(fcmSendDTO);
        return CustomResponse.onSuccess("성공적으로 알림이 전송되었습니다.");
    }
}
