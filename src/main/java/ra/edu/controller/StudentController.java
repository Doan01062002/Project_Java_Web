package ra.edu.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ra.edu.dto.ChangePasswordDTO;
import ra.edu.dto.UpdateProfileDTO;
import ra.edu.dto.StudentDTO;
import ra.edu.entity.Student;
import ra.edu.service.StudentService;

import java.time.LocalDate;

@Controller
@RequestMapping("/profile")
public class StudentController {

    @Autowired
    private StudentService studentService;

    // Hiển thị trang profile
    @GetMapping
    public String showProfilePage(
            @RequestParam(name = "showPasswordModal", required = false) Boolean showPasswordModal,
            HttpSession session,
            Model model) {
        StudentDTO loggedInUser = (StudentDTO) session.getAttribute("loggedInUser");

        if (loggedInUser == null || Boolean.TRUE.equals(loggedInUser.getRole())) {
            return "redirect:/login_form";
        }

        Student student = studentService.getStudentById(loggedInUser.getId());

        UpdateProfileDTO profileDTO = new UpdateProfileDTO();
        profileDTO.setName(student.getName());
        profileDTO.setPhone(student.getPhone());
        profileDTO.setEmail(student.getEmail());
        profileDTO.setDob(student.getDob().toString());

        // Nếu có lỗi đổi mật khẩu mang qua redirect, giữ lại đối tượng và lỗi
        if (!model.containsAttribute("changePasswordDTO")) {
            model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        }
        // Đảm bảo bindingResult hiển thị khi có lỗi đổi mật khẩu
        if (!model.containsAttribute("org.springframework.validation.BindingResult.changePasswordDTO")) {
            model.addAttribute("org.springframework.validation.BindingResult.changePasswordDTO", null);
        }

        model.addAttribute("profileDTO", profileDTO);
        model.addAttribute("gender", student.getSex() ? "Male" : "Female");
        model.addAttribute("showPasswordModal", Boolean.TRUE.equals(showPasswordModal) || model.asMap().containsKey("showPasswordModal"));

        return "profile";
    }

    // Xử lý cập nhật thông tin
    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("profileDTO") UpdateProfileDTO profileDTO,
                                BindingResult bindingResult,
                                HttpSession session,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        StudentDTO loggedInUser = (StudentDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login_form";
        }

        // Check duplicate email, phone (ngoại trừ của chính mình)
        boolean duplicateEmail = studentService.isEmailDuplicate(profileDTO.getEmail(), loggedInUser.getId());
        boolean duplicatePhone = studentService.isPhoneDuplicate(profileDTO.getPhone(), loggedInUser.getId());

        if (duplicateEmail) {
            bindingResult.rejectValue("email", "error.profileDTO", "Email này đã được sử dụng");
        }
        if (duplicatePhone) {
            bindingResult.rejectValue("phone", "error.profileDTO", "Số điện thoại này đã được sử dụng");
        }

        if (bindingResult.hasErrors()) {
            Student student = studentService.getStudentById(loggedInUser.getId());
            model.addAttribute("profileDTO", profileDTO);
            model.addAttribute("gender", student.getSex() ? "Male" : "Female");
            // Để modal không bật lên khi update thông tin
            model.addAttribute("showPasswordModal", false);
            return "profile";
        }

        Student student = studentService.getStudentById(loggedInUser.getId());
        student.setName(profileDTO.getName());
        student.setPhone(profileDTO.getPhone());
        student.setEmail(profileDTO.getEmail());
        student.setDob(LocalDate.parse(profileDTO.getDob()));

        studentService.updateStudent(student);

        // Cập nhật lại thông tin trong session
        loggedInUser.setName(profileDTO.getName());
        loggedInUser.setPhone(profileDTO.getPhone());
        loggedInUser.setEmail(profileDTO.getEmail());
        loggedInUser.setDob(LocalDate.parse(profileDTO.getDob()));
        session.setAttribute("loggedInUser", loggedInUser);

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
        return "redirect:/profile";
    }

    // Xử lý đổi mật khẩu
    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDTO") ChangePasswordDTO changePasswordDTO,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        StudentDTO loggedInUser = (StudentDTO) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/login_form";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.changePasswordDTO", bindingResult);
            redirectAttributes.addFlashAttribute("changePasswordDTO", changePasswordDTO);
            redirectAttributes.addFlashAttribute("showPasswordModal", true); // Hiển thị modal khi có lỗi
            return "redirect:/profile";
        }

        // Kiểm tra mật khẩu cũ
        if (!studentService.checkPassword(loggedInUser.getId(), changePasswordDTO.getOldPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu cũ không đúng");
            redirectAttributes.addFlashAttribute("showPasswordModal", true);
            return "redirect:/profile";
        }

        // Kiểm tra mật khẩu mới và xác nhận mật khẩu
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Xác nhận mật khẩu không khớp");
            redirectAttributes.addFlashAttribute("showPasswordModal", true);
            return "redirect:/profile";
        }

        // Cập nhật mật khẩu mới
        studentService.changePassword(loggedInUser.getId(), changePasswordDTO.getNewPassword());

        redirectAttributes.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
        return "redirect:/profile";
    }
}