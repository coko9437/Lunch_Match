package com.example.kmj.controller;

import com.example.kmj.dto.ReportRequestDTO;
import com.example.kmj.entity.Report;
import com.example.kmj.repository.ReportRepository;
import com.example.kmj.repository.BoardRepository;
import com.example.kmj.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReportController {

    private final ReportRepository reportRepository;
    private final BoardService boardService;
    private final BoardRepository boardRepository;

    @PostMapping("/report/submit")
    public String submitReport(@ModelAttribute ReportRequestDTO reportRequestDTO, Principal principal, RedirectAttributes rttr) {

        String reporter = (principal != null) ? principal.getName() : "anonymous";

        // 중복 신고 확인
        boolean exists = reportRepository.findByBoardIdAndReporter(reportRequestDTO.getBoardId(), reporter).isPresent();
        if (exists) {
            rttr.addFlashAttribute("error", "이미 신고한 게시글입니다.");
            return "redirect:/board/list";
        }

        // 신고 사유에 비속어 필터링 적용
        String filteredReason = boardService.filterBadWordsForUser(reportRequestDTO.getReason());

        // 비속어 포함 여부 확인 (관리자 알림용)
        String adminMessage = boardService.checkBadWordsForAdmin(reportRequestDTO.getReason());

        // 신고 정보 저장
        Report report = new Report();
        report.setBoardId(reportRequestDTO.getBoardId());
        report.setReporter(reporter);
        report.setReason(filteredReason); // 필터링된 사유 저장

        // 비속어가 포함된 경우 상태를 별도로 표시할 수 있음
        if (adminMessage != null) {
            // 로그나 별도 처리 로직 추가 가능
            System.out.println("신고 사유에 부적절한 내용 포함: " + adminMessage);
        }

        reportRepository.save(report);

        // ✅ 신고 저장 후 누적 횟수 확인
        Long reportCount = reportRepository.countReportsByBoardId(reportRequestDTO.getBoardId());

        if (reportCount >= 3) {
            boardRepository.findById(reportRequestDTO.getBoardId()).ifPresent(board -> {
                board.blindPost("신고 누적");
                boardRepository.save(board);
            });
            rttr.addFlashAttribute("success", "신고가 접수되었습니다. 신고 누적으로 인해 게시글이 블라인드 처리되었습니다.");
            System.out.println("게시글 ID " + reportRequestDTO.getBoardId() + "이(가) 블라인드 처리되었습니다. (신고 " + reportCount + "회)");
        } else {
            rttr.addFlashAttribute("success", "신고가 접수되었습니다. 검토 후 처리하겠습니다.");
        }
        return "redirect:/board/list";
    }

    // 관리자용 신고 목록 조회
    @GetMapping("/admin/reports")
    public String adminReports(Model model) {
        List<Report> pendingReports = reportRepository.findPendingReports();
        List<Report> allReports = reportRepository.findAllOrderByReportDateDesc();

        model.addAttribute("pendingReports", pendingReports);
        model.addAttribute("allReports", allReports);

        return "admin/reports";
    }

    // 신고 상태 업데이트
    /**
     * 관리자용으로 신고 상태를 업데이트하는 POST 요청을 처리합니다.
     * @param reportId 업데이트할 신고의 ID
     * @param status 변경할 신고 상태 (PENDING, REVIEWED, RESOLVED)
     * @param rttr 리다이렉트 시 사용할 속성
     * @return 관리자 신고 목록 페이지로 리다이렉트
     */
    @PostMapping("/admin/report/update-status")
    public String updateReportStatus(@RequestParam Long reportId,
                                     @RequestParam Report.ReportStatus status,
                                     RedirectAttributes rttr) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다."));

        report.setStatus(status);
        reportRepository.save(report);

        rttr.addFlashAttribute("success", "신고 상태가 업데이트되었습니다.");
        return "redirect:/admin/reports";
    }
}