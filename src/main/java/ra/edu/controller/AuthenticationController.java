package ra.edu.controller;

import ra.edu.dto.LoginDTO;
import ra.edu.dto.StudentDTO;
import ra.edu.service.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.service.CourseService;
import ra.edu.service.EnrollmentService;
import ra.edu.service.StudentService;

import java.util.List;

@Controller
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private CourseService courseService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/login_form")
    public String loginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        return "login";
    }

    @PostMapping("/login_account")
    public String login(@Valid @ModelAttribute("loginDTO") LoginDTO loginDTO,
                        BindingResult bindingResult,
                        Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        StudentDTO authenticatedStudent = authenticationService.login(loginDTO.getEmail(), loginDTO.getPassword());

        if (authenticatedStudent != null) {

            if (authenticatedStudent.isStatus()){
                session.setAttribute("loggedInUser", authenticatedStudent);

                if (authenticatedStudent.getRole() != null && authenticatedStudent.getRole()) {
                    return "redirect:/dashboard"; // Admin vẫn về dashboard
                } else {
                    return "redirect:/courses/list"; // User về trang danh sách khóa học
                }
            }else {
                model.addAttribute("messageErorrLock", "Tài khoản của bạn đã bị khoá");
                return "login";
            }

        } else {
            model.addAttribute("messageError", "Email hoặc mật khẩu không đúng. Vui lòng thử lại.");
            return "login";
        }
    }

    @GetMapping("/register_form")
    public String registerForm(Model model) {
        StudentDTO studentDTO = new StudentDTO();
        model.addAttribute("studentDTO", studentDTO);
        return "register";
    }

    @PostMapping("/register_save")
    public String register(@Valid @ModelAttribute("studentDTO") StudentDTO studentDTO
            , BindingResult bindingResult, Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        authenticationService.register(studentDTO);
        redirectAttributes.addFlashAttribute("messageSuccess", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
        return "redirect:/login_form";
    }

    // --- TRANG DASHBOARD CHO ADMIN (ĐÃ CẬP NHẬT) ---
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        StudentDTO loggedInUser = (StudentDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null || !Boolean.TRUE.equals(loggedInUser.getRole())) {
            return "redirect:/login_form";
        }

        // Tổng số học viên (chỉ student, không tính admin)
        int totalStudent = studentService.countStudents("");
        // Tổng số khóa học
        long totalCourse = courseService.countTotalCourses();
        // Tổng số lượt đăng ký (được xác nhận)
        long totalEnrollment = enrollmentService.countTotalEnrollments();

        // Thống kê học viên theo từng khóa
        List<Object[]> studentByCourse = enrollmentService.countStudentByCourse();

        // Top 5 khóa học đông sinh viên nhất
        List<Object[]> top5Courses = enrollmentService.top5CoursesByEnrollment();

        model.addAttribute("totalStudent", totalStudent);
        model.addAttribute("totalCourse", totalCourse);
        model.addAttribute("totalEnrollment", totalEnrollment);
        model.addAttribute("studentByCourse", studentByCourse);
        model.addAttribute("top5Courses", top5Courses);

        return "home_admin";
    }


    // @GetMapping("/list_course")
    // public String showHomePage(HttpSession session, Model model) {
    //     StudentDTO loggedInUser = (StudentDTO) session.getAttribute("loggedInUser");
    //     if (loggedInUser == null) {
    //         return "redirect:/login"; // Nếu chưa đăng nhập, về trang login
    //     }
    //     model.addAttribute("user", loggedInUser);
    //     return "list_course";
    // }

    // --- ĐĂNG XUẤT (ĐÃ CẬP NHẬT) ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login_form";
    }
}