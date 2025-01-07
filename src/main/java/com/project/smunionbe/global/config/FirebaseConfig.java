package com.project.smunionbe.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    // application.yml에서 Firebase 서비스 계정 파일 경로를 가져옴
    @Value("${firebase.config.path}")
    private String keyPath;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        // ClassPathResource로 리소스 경로에서 파일을 불러옴
        ClassPathResource resource = new ClassPathResource(keyPath);

        // Firebase 옵션 설정
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .build();

        // FirebaseApp을 초기화하고 빈으로 등록
        return FirebaseApp.initializeApp(options);
    }
}