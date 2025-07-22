package com.busanit501.yyjproject.repository.search;

import com.busanit501.yyjproject.domain.Review;
import com.busanit501.yyjproject.dto.PageRequestDTO;
import org.springframework.data.domain.Page;

public interface ReviewSearch {
    Page<Review> searchAll(PageRequestDTO pageRequestDTO);
}
