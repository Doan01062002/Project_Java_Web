package ra.edu.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ra.edu.dto.StudentDTO;
import ra.edu.service.EnrollmentUserService;

@Controller
@RequestMapping("/enrollments")
public class EnrollmentUserController {

    @Autowired
    private EnrollmentUserService enrollmentUserService;

    private static final int PAGE_SIZE = 5;


    @GetMapping("/list")
    public String list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model, HttpSession session) {

        StudentDTO user = (StudentDTO) session.getAttribute("loggedInUser");
        if (user == null || Boolean.TRUE.equals(user.getRole())) {
            return "redirect:/login_form";
        }

        final int SIZE = 5;

        var enrollments = enrollmentUserService.searchEnrollments(
                user.getId(), keyword, status, page, SIZE);

        long total = enrollmentUserService.countSearchEnrollments(
                user.getId(), keyword, status);

        model.addAttribute("enrollments", enrollments);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) total / SIZE));

        return "list_enrollment";
    }

}