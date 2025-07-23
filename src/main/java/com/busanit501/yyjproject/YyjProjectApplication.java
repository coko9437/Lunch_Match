package com.busanit501.yyjproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class YyjProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(YyjProjectApplication.class, args);
    }

}
