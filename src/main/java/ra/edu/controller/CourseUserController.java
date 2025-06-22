package ra.edu.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
public class CourseUserController {

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
            @RequestParam(name = "confirmId", required = false) Integer confirmId,
            HttpSession session,
            Model model) {

        int pageSize = 5;
        List<Course> courses = courseService.searchAndSortCourses(keyword, "id", "asc", page, pageSize);
        long totalCourses = courseService.countSearchedCourses(keyword);
        int totalPages = (int) Math.ceil((double) totalCourses / pageSize);

        StudentDTO loggedInUser = (StudentDTO) session.getAttribute("loggedInUser");
        List<Integer> registeredCourseIds = null;
        boolean isAdmin = false;
        if (loggedInUser != null) {
            registeredCourseIds = enrollmentService.getRegisteredCourseIds(loggedInUser.getId());
        }
        if (loggedInUser != null && "true".equalsIgnoreCase(loggedInUser.getRole().toString())) {
            isAdmin = true;
        }
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("listCourse", courses);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("registeredCourseIds", registeredCourseIds);

        // Nếu có confirmId => bật modal xác nhận
        if (confirmId != null) {
            Course course = courseService.getCourseById(confirmId);
            model.addAttribute("showConfirmModal", true);
            model.addAttribute("confirmCourse", course);
        }

        // Đề xuất khóa học cùng giáo viên đã đăng ký (nếu đã đăng nhập)
        List<Course> suggestedByInstructor = null;
        if (loggedInUser != null) {
            suggestedByInstructor = enrollmentService.suggestCoursesByInstructor(loggedInUser.getId());
        }
        model.addAttribute("suggestedByInstructor", suggestedByInstructor);

        // Đề xuất top khóa học nổi bật (nhiều người đăng ký nhất)
        List<Course> topCourses = enrollmentService.suggestTopCourses(5);
        model.addAttribute("topCourses", topCourses);

        return "list_course";
    }

    // Đổi sang GET để redirect tiện lợi hơn (có thể dùng POST cũng được)
    @PostMapping("/register-confirm")
    public String confirmRegister(@RequestParam("courseId") int courseId,
                                  @RequestParam(name = "page", defaultValue = "1") int page,
                                  @RequestParam(name = "keyword", required = false) String keyword) {
        // Chuyển hướng về list kèm confirmId (để Thymeleaf bật modal)
        if (keyword == null) keyword = "";
        return "redirect:/courses/list?page=" + page + "&keyword=" + keyword + "&confirmId=" + courseId;
    }

    @PostMapping("/register")
    public String registerCourse(@RequestParam("courseId") int courseId,
                                 @RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "keyword", required = false) String keyword,
                                 HttpSession session, RedirectAttributes redirectAttributes) {
        StudentDTO loggedInUser = (StudentDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("error", "Bạn cần đăng nhập để đăng ký khóa học!");
            return "redirect:/login_form";
        }
        Student student = authenticationService.checkExistUserName(loggedInUser.getUsername());
        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản sinh viên!");
            return "redirect:/courses/list";
        }
        if (enrollmentService.hasEnrolled(student.getId(), courseId)) {
            redirectAttributes.addFlashAttribute("message", "Bạn đã đăng ký khóa học này rồi!");
            return "redirect:/courses/list";
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(courseService.getCourseById(courseId));
        enrollment.setStudent(student);
        enrollment.setRegisteredAt(LocalDateTime.now());
        enrollment.setStatus(Enrollment.Status.WAITING);
        enrollmentService.save(enrollment);

        redirectAttributes.addFlashAttribute("message", "Đăng ký thành công!");
        return "redirect:/courses/list";
    }
}