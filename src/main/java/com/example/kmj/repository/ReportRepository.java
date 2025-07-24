package com.example.kmj.repository;

import com.example.kmj.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // 특정 게시글에 대한 특정 사용자의 신고 기록 찾기 (중복 신고 방지)
    Optional<Report> findByBoardIdAndReporter(Long boardId, String reporter);

    // 특정 게시글에 대한 모든 신고 목록
    List<Report> findByBoardId(Long boardId);

    // 특정 사용자가 신고한 모든 목록
    List<Report> findByReporter(String reporter);

    // 상태별 신고 목록 조회
    List<Report> findByStatus(Report.ReportStatus status);

    // 특정 게시글의 신고 횟수 조회
    @Query("SELECT COUNT(r) FROM Report r WHERE r.boardId = :boardId")
    Long countReportsByBoardId(@Param("boardId") Long boardId);

    // 최근 신고 목록 조회 (관리자용)
    @Query("SELECT r FROM Report r ORDER BY r.reportDate DESC")
    List<Report> findAllOrderByReportDateDesc();

    // 처리되지 않은 신고 목록 조회
    /**
     * 처리되지 않은 (PENDING) 상태의 신고 목록을 오래된 신고 날짜 순으로 조회합니다.
     * @return 처리 대기 중인 신고 목록
     */
    @Query("SELECT r FROM Report r WHERE r.status = 'PENDING' ORDER BY r.reportDate ASC")
    List<Report> findPendingReports();
}