package com.example.kmj.repository;

import com.example.kmj.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * `Board` 엔티티에 대한 데이터베이스 접근을 처리하는 리포지토리 인터페이스입니다.
 * Spring Data JPA의 `JpaRepository`를 상속받아 기본적인 CRUD 기능을 제공합니다.
 */
public interface BoardRepository extends JpaRepository<Board, Long> {
}
