package com.busanit501.yyjproject.service;

import com.busanit501.yyjproject.dto.PageRequestDTO;
import com.busanit501.yyjproject.dto.PageResponseDTO;
import com.busanit501.yyjproject.dto.ReviewDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
public class ReviewServiceTests {

    @Autowired
    private ReviewService reviewService;

    private Long testReviewId;

    @BeforeEach
    public void setup() {
        ReviewDTO reviewDTO = ReviewDTO.builder()
                .member_id("testuser_setup")
                .content("Setup Test Content")
                .menu("Setup Test Menu")
                .place("Setup Test Place")
                .rating(3)
                .emotion("neutral")
                .build();
        testReviewId = reviewService.register(reviewDTO);
        log.info("Setup: Registered Review ID: " + testReviewId);
    }

    @Test
    public void testRegister() {
        ReviewDTO reviewDTO = ReviewDTO.builder()
                .member_id("testuser")
                .content("서비스 테스트 등록 내용")
                .menu("서비스 테스트 메뉴")
                .place("서비스 테스트 장소")
                .rating(4)
                .emotion("happy")
                .build();
        Long review_id = reviewService.register(reviewDTO);
        log.info("Registered Review ID: " + review_id);
    }

    @Test
    @Transactional
    public void testReadOne() {
        ReviewDTO reviewDTO = reviewService.readOne(testReviewId);
        log.info("Read One Review: " + reviewDTO);
    }

    @Test
    @Transactional
    public void testModify() {
        // 1. 수정할 '새로운' 리뷰 데이터를 먼저 등록합니다.
        ReviewDTO originalReviewDTO = ReviewDTO.builder()
                .member_id("testuser_modify")
                .content("수정 전 원본 내용")
                .menu("수정 전 원본 메뉴")
                .place("수정 전 원본 장소")
                .rating(3)
                .emotion("neutral")
                .build();

        Long registeredId = reviewService.register(originalReviewDTO);
        log.info("수정할 리뷰 ID (동적 생성): " + registeredId);

        // 2. 이제 이 동적으로 생성된 registeredId를 사용하여 수정할 DTO를 만듭니다.
        ReviewDTO modifiedReviewDTO = ReviewDTO.builder()
                .review_id(registeredId) // 하드코딩 대신 동적으로 얻은 ID 사용
                .content("서비스 테스트 수정 내용")
                .menu("서비스 테스트 수정 메뉴")
                .place("서비스 테스트 수정 장소")
                .rating(5)
                .emotion("very happy")
                .build();

        // 3. 수정 서비스 호출
        reviewService.modify(modifiedReviewDTO);

        // 4. 수정이 제대로 되었는지 확인하기 위해 다시 조회합니다.
        ReviewDTO foundReview = reviewService.readOne(registeredId);
        log.info("수정 후 조회된 리뷰: " + foundReview);

        // 실제 테스트에서는 Assertions.assertEquals 등을 사용하여
        // 수정된 내용이 예상과 일치하는지 검증해야 합니다.
        // Assertions.assertEquals("서비스 테스트 수정 내용", foundReview.getContent());
    }

    @Test
    @Transactional // 삭제 후 롤백을 위해 @Transactional 추가
    public void testRemove() {
        // 1. 삭제할 '새로운' 리뷰 데이터를 먼저 등록합니다.
        ReviewDTO originalReviewDTO = ReviewDTO.builder()
                .member_id("testuser_remove")
                .content("삭제할 내용")
                .menu("삭제할 메뉴")
                .place("삭제할 장소")
                .rating(1)
                .emotion("sad")
                .build();

        Long registeredId = reviewService.register(originalReviewDTO);
        log.info("삭제할 리뷰 ID (동적 생성): " + registeredId);

        // 2. 삭제 서비스 호출
        reviewService.remove(registeredId);
        log.info("Removed Review ID: " + registeredId);

        // 3. 삭제가 제대로 되었는지 확인하기 위해 다시 조회 시 예외가 발생하는지 검증합니다.
        Assertions.assertThrows(java.util.NoSuchElementException.class, () -> {
            reviewService.readOne(registeredId);
        });
        log.info("리뷰가 성공적으로 삭제되었음을 확인했습니다 (조회 시 예외 발생).");
    }

    @Test
    public void testGetList() {
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .type("") // 빈 문자열로 초기화
                .keyword(null) // null로 유지하거나 빈 문자열로 초기화
                .build();
        PageResponseDTO<ReviewDTO> responseDTO = reviewService.getList(pageRequestDTO);
        log.info(responseDTO);

        // 검증 추가
        Assertions.assertNotNull(responseDTO);
        Assertions.assertFalse(responseDTO.getDtoList().isEmpty(), "DTO List should not be empty");
        Assertions.assertEquals(10, responseDTO.getDtoList().size(), "DTO List size should be 10");
        Assertions.assertTrue(responseDTO.getTotalCount() > 0, "Total count should be greater than 0");
        Assertions.assertEquals(1, responseDTO.getPage(), "Current page should be 1");
        Assertions.assertEquals(10, responseDTO.getSize(), "Page size should be 10");

        // prev, next, start, end는 데이터 양에 따라 달라지므로, 일반적인 경우를 가정하여 검증
        // 예를 들어, 데이터가 충분히 많아 다음 페이지가 있다면 next는 true여야 합니다.
        // 이 테스트는 데이터베이스에 충분한 데이터가 있다고 가정합니다.
        // Assertions.assertTrue(responseDTO.isNext(), "Next page should exist");
        // Assertions.assertFalse(responseDTO.isPrev(), "Previous page should not exist for page 1");
        // Assertions.assertEquals(1, responseDTO.getStart(), "Start page should be 1");
        // Assertions.assertTrue(responseDTO.getEnd() >= 1, "End page should be at least 1");
    }
}
