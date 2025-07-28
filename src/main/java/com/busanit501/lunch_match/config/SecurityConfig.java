package com.busanit501.lunch_match.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("=====SecurityConfig=====");

        // CSRF임.
        // REST API 개발중에는 잠시 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // 특정 경로에 대한 접근 권한 설정
        http.authorizeHttpRequests(authorize -> authorize
                // 회원가입 API는 인증 없이 접근 허용
                .requestMatchers("/api/members/signup").permitAll()
                // 이메일 인증 API는 인증 없이 접근 허용
                .requestMatchers("/api/auth/**").permitAll()
                // H2 Console, Swagger UI, 업로드 파일 접근은 인증 없이 허용
                .requestMatchers("/h2-console/**", "/swagger-ui/**", "/v3/api-docs/**", "/upload/**").permitAll()
                // 그 외 모든 요청은 인증 필요 (로그인 필요)
                .anyRequest().authenticated()
        );
        // H2 Console 프레임 허용 (개발 환경에서만 필요)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // TODO: 추후 로그인, 로그아웃, 예외 처리 등을 추가

        return http.build();
    }
}

