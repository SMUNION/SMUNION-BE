package com.project.smunionbe.domain.member.controller;

import com.project.smunionbe.domain.member.dto.request.MemberRequestDTO;
import com.project.smunionbe.domain.member.service.MemberService;
import com.project.smunionbe.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "회원 API", description = "-")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    @Operation(
            summary = "회원가입 API",
            description = "회원가입 API 입니다."
    )
    public ResponseEntity<CustomResponse<String>> signup(@RequestBody MemberRequestDTO.CreateMemberDTO request) {
        memberService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CustomResponse.onSuccess(HttpStatus.CREATED, "회원가입이 완료되었습니다."));
    }

}
