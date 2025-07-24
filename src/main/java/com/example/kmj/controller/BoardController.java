package com.example.kmj.controller;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class BoardController {

    /**
     * 게시글 목록 페이지를 반환합니다.
     * @return 게시글 목록 뷰 이름
     */
    @GetMapping("/board/list")
    public String listPage() {
        return "board/list";
    }
}