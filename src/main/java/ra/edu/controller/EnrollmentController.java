package ra.edu.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ra.edu.dto.StudentDTO;
import ra.edu.entity.Course;
import ra.edu.entity.Enrollment;
import ra.edu.entity.Student;
import ra.edu.service.AuthenticationService;
import ra.edu.service.CourseService;
import ra.edu.service.EnrollmentService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/courses")
public class EnrollmentController {

    @Autowired
    private CourseService courseService;
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/list")
    public String listCourses(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "keyword", required = false) String keyword,
            HttpSession session,
            Model model) {

        int pageSize = 10;
        List<Course> courses = courseService.searchAndSortCourses(keyword, "id", "asc", page, pageSize);
        long totalCourses = courseService.countSearchedCourses(keyword);
        int totalPages = (int) Math.ceil((double) totalCourses / pageSize);

        StudentDTO loggedInUser = (StudentDTO) session.getAttribute("loggedInUser");
        List<Integer> registeredCourseIds = null;
        if (loggedInUser != null) {
            registeredCourseIds = enrollmentService.getRegisteredCourseIds(loggedInUser.getId());
        }

        model.addAttribute("listCourse", courses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("registeredCourseIds", registeredCourseIds);

        return "list_course";
    }

    @PostMapping("/register")
    public String registerCourse(@RequestParam("courseId") int courseId, HttpSession session, Model model) {
        StudentDTO loggedInUser = (StudentDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            model.addAttribute("error", "Bạn cần đăng nhập để đăng ký khóa học!");
            return "redirect:/login_form";
        }
        // Lấy Student từ AuthenticationService (theo id)
        Student student = authenticationService.checkExistUserName(loggedInUser.getUsername());
        if (student == null) {
            model.addAttribute("error", "Không tìm thấy tài khoản sinh viên!");
            return "redirect:/courses/list";
        }
        // Kiểm tra đã đăng ký chưa
        if (enrollmentService.hasEnrolled(student.getId(), courseId)) {
            model.addAttribute("message", "Bạn đã đăng ký khóa học này rồi!");
            return "redirect:/courses/list";
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(courseService.getCourseById(courseId));
        enrollment.setStudent(student);
        enrollment.setRegisteredAt(LocalDateTime.now());
        enrollment.setStatus(Enrollment.Status.WAITING);
        enrollmentService.save(enrollment);

        model.addAttribute("message", "Đăng ký thành công!");
        return "redirect:/courses/list";
    }
}