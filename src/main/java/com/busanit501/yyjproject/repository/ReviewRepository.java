package com.busanit501.yyjproject.repository;

import com.busanit501.yyjproject.domain.Review;
import com.busanit501.yyjproject.repository.search.ReviewSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// JpaRepository, QuerydslPredicateExecutor, ReviewSearch 모두 상속
public interface ReviewRepository extends JpaRepository<Review, Long>, QuerydslPredicateExecutor<Review>, ReviewSearch {
    @Query("select r from Review r left join fetch r.fileList where r.review_id = :review_id")
    java.util.Optional<Review> findByIdWithFiles(@Param("review_id") Long review_id);
}