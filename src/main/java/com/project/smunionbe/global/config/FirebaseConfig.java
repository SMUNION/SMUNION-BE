package com.project.smunionbe.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
@Slf4j
public class FirebaseConfig {

    // application.yml에서 Firebase 서비스 계정 파일 경로를 가져옴
    @Value("${firebase.config.path}")
    private String keyPath;

    @Value("${firebase.project-id}")
    private String projectId;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        try {
            // 이미 초기화된 앱이 있는지 확인
            if (!FirebaseApp.getApps().isEmpty()) {
                log.info("Firebase App이 이미 초기화되어 있습니다.");
                return FirebaseApp.getInstance();
            }

            // ClassPathResource로 리소스 경로에서 파일을 불러옴
            ClassPathResource resource = new ClassPathResource(keyPath);
            log.info("Firebase 서비스 계정 키 로드: {}", keyPath);

            // Firebase 옵션 설정 - 프로젝트 ID 명시적 설정 추가
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                    .setProjectId(projectId)  // 프로젝트 ID 명시적 설정
                    .build();

            // FirebaseApp을 초기화하고 빈으로 등록
            FirebaseApp app = FirebaseApp.initializeApp(options);
            log.info("Firebase App이 성공적으로 초기화되었습니다. 프로젝트 ID: {}", projectId);
            return app;
        } catch (Exception e) {
            log.error("Firebase 초기화 중 오류 발생", e);
            throw e;
        }
    }
}