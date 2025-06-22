package ra.edu.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.StudentDTO;
import ra.edu.entity.Enrollment;
import ra.edu.service.EnrollmentService;

@Controller
@RequestMapping("/admin/enrollments")
public class EnrollmentAdminController {

    @Autowired
    private EnrollmentService enrollmentAdminService;

    private static final int PAGE_SIZE = 8;

    @GetMapping("/list")
    public String list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model, HttpSession session) {

        StudentDTO user = (StudentDTO) session.getAttribute("loggedInUser");
        if (user == null || !Boolean.TRUE.equals(user.getRole())) {
            return "redirect:/login_form";
        }

        // Xử lý tìm kiếm và phân trang
        var enrollments = enrollmentAdminService.searchAdminEnrollments(keyword, status, page, PAGE_SIZE);
        long total = enrollmentAdminService.countAdminEnrollments(keyword, status);

        model.addAttribute("enrollments", enrollments);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) total / PAGE_SIZE));

        return "enrollment_manager";
    }

    @PostMapping("/approve")
    public String approve(@RequestParam("id") int enrollmentId, RedirectAttributes redirectAttributes) {
        if (enrollmentAdminService.approveEnrollment(enrollmentId)) {
            redirectAttributes.addFlashAttribute("successMessage", "Duyệt thành công.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Duyệt thất bại. Đăng ký không tồn tại hoặc không ở trạng thái WAITING.");
        }
        return "redirect:/admin/enrollments/list";
    }

    @PostMapping("/deny")
    public String deny(@RequestParam("id") int enrollmentId, RedirectAttributes redirectAttributes) {
        if (enrollmentAdminService.denyEnrollment(enrollmentId)) {
            redirectAttributes.addFlashAttribute("successMessage", "Từ chối thành công.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Từ chối thất bại. Đăng ký không tồn tại hoặc không ở trạng thái WAITING.");
        }
        return "redirect:/admin/enrollments/list";
    }
}