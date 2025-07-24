package com.busanit501.lunch_match.controller;

import com.busanit501.lunch_match.Service.EmailAuthService;
import com.busanit501.lunch_match.dto.EmailAuthRequestDTO;
import com.busanit501.lunch_match.dto.EmailAuthVerifyDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class EmailAuthController {
    private final EmailAuthService emailAuthService;

    // 인증번호 요청
    @PostMapping("/send-code")
    public ResponseEntity<String> sendCode(@RequestBody EmailAuthRequestDTO request) {
        emailAuthService.sendAuthCode(request.getEmail());
        return ResponseEntity.ok("인증번호가 전송되었습니다.");
    }

    // 인증번호 검증
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody EmailAuthVerifyDTO request) {
        boolean result = emailAuthService.verifyAuthCode(request.getEmail(), request.getAuthCode());
        return result ? ResponseEntity.ok("인증 성공") :
                ResponseEntity.badRequest().body("인증번호가 일치하지 않거나 만료되었습니다.");
    }
}
