package com.busanit501.lunch_match.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test")
    public String showPage() {
        return "test"; // templates/test.html 로 변환
    }
}
