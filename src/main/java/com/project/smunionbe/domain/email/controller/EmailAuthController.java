package com.project.smunionbe.domain.email.controller;

import com.project.smunionbe.domain.email.dto.request.EmailReqDTO;
import com.project.smunionbe.domain.email.service.EmailAuthService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
@Tag(name = "이메일 인증 API", description = "이메일 인증 API 입니다.")
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    @Operation(method = "POST", summary = "회원가입 시 이메일에 인증 코드 전송", description = "이메일에 인증 코드를 전송합니다. 회원가입 시 사용.")
    @PostMapping("/send/signup")
    public CustomResponse<String> sendEmailAuthCodeToSignUp(@RequestBody EmailReqDTO.SendEmailRequestDTO request) {
        emailAuthService.sendSignUpEmailAuthCode(request.email()); // 이메일 인증 코드 전송 로직을 실행
        return CustomResponse.onSuccess("해당 이메일에 인증 코드가 전송되었습니다.");
    }

    @Operation(method = "POST", summary = "인증 코드 검증", description = "사용자가 작성한 인증 코드를 검증합니다.")
    @PostMapping("/verify")
    public CustomResponse<?> verifyEmailAuthCode(@RequestBody EmailReqDTO.VerifyEmailRequestDTO request) {
        // 이메일 인증 코드를 검증하는 요청을 처리합니다.
        emailAuthService.verifyEmailAuthCode(request.email(), request.code()); // 이메일 인증 코드 검증 로직을 실행
        return CustomResponse.onSuccess("성공적으로 이메일 인증이 완료되었습니다.");
    }
}
