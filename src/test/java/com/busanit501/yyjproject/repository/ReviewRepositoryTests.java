package com.busanit501.yyjproject.repository;

import com.busanit501.yyjproject.domain.Review;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository reviewRepository;

    @Disabled
    @Test
    public void testInsert() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Review review = Review.builder()
                    .member_id("user" + i)
                    .content("맛있는 리뷰 " + i)
                    .menu("메뉴 " + i)
                    .place("장소 " + i)
                    .rating((i % 5) + 1) // 1~5점
                    .emotion("emotion" + (i % 3)) // 0,1,2
                    .build();
            Review result = reviewRepository.save(review);
            log.info("Review ID: " + result.getReview_id());
        });
    }

    @Test
    @Transactional
    public void testSelect() {
        Long review_id = 100L; // 실제 존재하는 ID로 변경 필요
        Optional<Review> result = reviewRepository.findById(review_id);
        Review review = result.orElseThrow();
        log.info(review);
    }

    @Test
    @Transactional
    public void testUpdate() {
        Long review_id = 100L; // 실제 존재하는 ID로 변경 필요
        Optional<Review> result = reviewRepository.findById(review_id);
        Review review = result.orElseThrow();

        review.change("수정된 내용", "수정된 메뉴", "수정된 장소", 5, "happy");

        reviewRepository.save(review);
        log.info(review);
    }

    @Test
    public void testDelete() {
        Long review_id = 1L; // 실제 존재하는 ID로 변경 필요
        reviewRepository.deleteById(review_id);
    }
}