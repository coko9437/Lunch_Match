package com.busanit501.yyjproject.service;

import com.busanit501.yyjproject.dto.PageRequestDTO;
import com.busanit501.yyjproject.dto.PageResponseDTO;
import com.busanit501.yyjproject.dto.ReviewDTO;

public interface ReviewService {
    Long register(ReviewDTO reviewDTO);
    ReviewDTO readOne(Long review_id);
    void modify(ReviewDTO reviewDTO);
    void remove(Long review_id);
    PageResponseDTO<ReviewDTO> getList(PageRequestDTO pageRequestDTO);
}
