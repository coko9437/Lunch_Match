package com.busanit501.yyjproject.repository;

import com.busanit501.yyjproject.domain.Review;
import com.busanit501.yyjproject.repository.search.ReviewSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

// JpaRepository, QuerydslPredicateExecutor, ReviewSearch 모두 상속
public interface ReviewRepository extends JpaRepository<Review, Long>, QuerydslPredicateExecutor<Review>, ReviewSearch {
}