package com.project.smunionbe.domain.notification.fcm.service.command;

import com.project.smunionbe.domain.notification.fcm.exception.FCMErrorCode;
import com.project.smunionbe.domain.notification.fcm.exception.FCMException;
import com.project.smunionbe.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMTokenService {

    private static final long TOKEN_EXPIRATION_DAYS = 30L;  // 토큰 만료일 상수(refreshToken 만료일과 동일하게 설정)
    private final RedisUtil redisUtil;

    // FCM 토큰 저장
    @Transactional
    public void saveFcmToken(String email, String fcmToken) {
        if (hasFcmToken(email)) {
            throw new FCMException(FCMErrorCode.FCMTOKEN_ALREADY_EXIST);
        }
        saveFcmTokenInRedis(email, fcmToken);
    }

    // FCM 토큰 삭제
    public void deleteFcmToken(String email) {
        String redisKey = generateRedisKey(email);
        boolean deleted = redisUtil.delete(redisKey);

        if (deleted) {
            log.info("[FcmTokenService] 사용자 {}의 FCM 토큰이 삭제되었습니다.", email);
        } else {
            log.warn("[FcmTokenService] 사용자 {}의 FCM 토큰을 찾을 수 없습니다.", email);
        }
    }

    // Redis 키 생성 로직
    private String generateRedisKey(String email) {
        return "FCM_TOKEN:" + email;
    }

    // FCM 토큰 존재 여부 확인
    public boolean hasFcmToken(String email) {
        String redisKey = generateRedisKey(email);
        return redisUtil.hasKey(redisKey);
    }

    // Redis에 FCM 토큰 저장
    private void saveFcmTokenInRedis(String email, String fcmToken) {
        String redisKey = generateRedisKey(email);
        redisUtil.save(redisKey, fcmToken, TOKEN_EXPIRATION_DAYS, TimeUnit.DAYS);
        log.info("[FcmTokenService] 사용자 {}의 FCM 토큰이 저장되었습니다.", email);
    }

    // Redis에서 FCM 토큰 조회
    public String getFcmToken(String email) {
        String redisKey = generateRedisKey(email);
        Object result = redisUtil.get(redisKey);

        if (result instanceof String) {
            return (String) result; // 명시적 타입 변환
        } else {
            log.warn("Redis에서 가져온 값이 String 타입이 아닙니다. 키: {}", redisKey);
            return null; // 또는 빈 문자열 반환
        }
    }
}
