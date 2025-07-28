package com.busanit501.lunch_match.controller;

import com.busanit501.lunch_match.dto.MemberSignupDTO;
import com.busanit501.lunch_match.dto.ProfileDTO;
import com.busanit501.lunch_match.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/members") //기본 URL 경로를 /api/members
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성
@Log4j2
public class MemberController {
    private final MemberService memberService;

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) //signup 경로로 들어오는 POST 요청을 처리
    public ResponseEntity<Map<String, String>> signupMember(
            @Valid @RequestPart("memberSignupDTO") MemberSignupDTO memberSignupDTO, // 회원 정보 DTO
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) { // required = false는 프로필 이미지 가 필수가아님.

        log.info("회원가입 요청 수신: {}", memberSignupDTO.getUsername());

        // ProfileDTO 생성 및 MultipartFile 설정
        ProfileDTO profileDTO = null;
        if (profileImage != null && !profileImage.isEmpty()) {
            profileDTO = ProfileDTO.builder()
                    .file(profileImage) // MultipartFile을 ProfileDTO 내부에 설정
                    .build();
        }

        try {
            Long memberId = memberService.registerMember(memberSignupDTO, profileDTO);
            log.info("회원가입 성공, Member ID: {}", memberId);

            // 성공 응답 반환
            return ResponseEntity.ok(Map.of("message", "회원가입이 성공적으로 완료되었습니다.", "memberId", memberId.toString()));

        } catch (IllegalArgumentException e) {
            // 사용자 입력 오류 (예: 중복 아이디, 비밀번호 불일치)
            log.warn("회원가입 실패 (유효성 검사 또는 중복): {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            // 그 외 서비스 계층에서 발생한 예외 (파일 업로드 실패 등)
            log.error("회원가입 중 서버 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "회원가입 중 서버 오류가 발생했습니다."));
        }
    }
}

