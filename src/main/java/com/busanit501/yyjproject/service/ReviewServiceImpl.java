package com.busanit501.yyjproject.service;

import com.busanit501.yyjproject.domain.Review;
import com.busanit501.yyjproject.dto.PageRequestDTO;
import com.busanit501.yyjproject.dto.PageResponseDTO;
import com.busanit501.yyjproject.dto.ReviewDTO;
import com.busanit501.yyjproject.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReviewServiceImpl implements ReviewService {

    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;

    @Override
    public Long register(ReviewDTO reviewDTO) {
        log.info("register...");
        Review review = modelMapper.map(reviewDTO, Review.class);

        // UploadResultDTO 리스트를 Review 엔티티의 fileList에 추가
        if (reviewDTO.getUploadFileNames() != null && !reviewDTO.getUploadFileNames().isEmpty()) {
            reviewDTO.getUploadFileNames().forEach(uploadResultDTO -> {
                review.getFileList().add(modelMapper.map(uploadResultDTO, com.busanit501.yyjproject.domain.UploadResult.class));
            });
        }

        Long review_id = reviewRepository.save(review).getReview_id();

        log.info("Review entity before saving: " + review);
        if (review.getFileList() != null) {
            review.getFileList().forEach(file -> log.info("  File in review entity: " + file.getFileName() + ", isImage: " + file.isImage()));
        }

        return review_id;
    }

    @Override
    public ReviewDTO readOne(Long review_id) {
        java.util.Optional<Review> result = reviewRepository.findById(review_id);
        Review review = result.orElseThrow();
        ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);
        return reviewDTO;
    }

    @Transactional
    @Override
    public void modify(ReviewDTO reviewDTO) {
        java.util.Optional<Review> result = reviewRepository.findById(reviewDTO.getReview_id());
        Review review = result.orElseThrow();

        review.change(reviewDTO.getContent(), reviewDTO.getMenu(), reviewDTO.getPlace(), reviewDTO.getRating(), reviewDTO.getEmotion());

        // 기존 파일 목록 삭제 후 새로운 파일 목록 추가
        review.getFileList().clear();
        if (reviewDTO.getUploadFileNames() != null && !reviewDTO.getUploadFileNames().isEmpty()) {
            reviewDTO.getUploadFileNames().forEach(uploadResultDTO -> {
                review.getFileList().add(modelMapper.map(uploadResultDTO, com.busanit501.yyjproject.domain.UploadResult.class));
            });
        }

        reviewRepository.save(review);
    }

    @Override
    public void remove(Long review_id) {
        reviewRepository.deleteById(review_id);
    }

    @Override
    public PageResponseDTO<ReviewDTO> getList(PageRequestDTO pageRequestDTO) {
        Page<Review> result = reviewRepository.searchAll(pageRequestDTO);

        List<ReviewDTO> dtoList = result.getContent().stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .collect(Collectors.toList());

        return PageResponseDTO.<ReviewDTO>builder()
                .dtoList(dtoList)
                .totalCount((int)result.getTotalElements())
                .page(pageRequestDTO.getPage()) // pageRequestDTO에서 page 가져오기
                .size(pageRequestDTO.getSize()) // pageRequestDTO에서 size 가져오기
                .build();
    }
}
