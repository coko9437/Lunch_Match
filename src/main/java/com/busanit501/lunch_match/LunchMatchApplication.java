package com.busanit501.lunch_match;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing // 부모 베이스 엔티티 클래스에 설정된 리스너가 동작을함.
public class LunchMatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(LunchMatchApplication.class, args);
    }

}
