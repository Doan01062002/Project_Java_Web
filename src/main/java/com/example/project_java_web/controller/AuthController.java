package com.example.project_java_web.controller;

import com.example.project_java_web.dto.UserDTO;
import com.example.project_java_web.entity.User;
import com.example.project_java_web.repository.UserRepository;
import com.example.project_java_web.service.UserService;
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

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // ... (Phần code cho /register không đổi)
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                                  BindingResult bindingResult,
                                  Model model) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            bindingResult.rejectValue("email", "error.userDTO", "Email đã tồn tại.");
        }
        if (userRepository.existsByPhone(userDTO.getPhone())) {
            bindingResult.rejectValue("phone", "error.userDTO", "Số điện thoại đã tồn tại.");
        }
        if (bindingResult.hasErrors()) {
            return "register";
        }
        userService.registerUser(userDTO);
        return "redirect:/login?success";
    }

    // --- ĐĂNG NHẬP (ĐÃ CẬP NHẬT LOGIC) ---
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute("userDTO") UserDTO userDTO,
                               Model model,
                               HttpSession session) {

        if (userDTO.getUsername().isBlank() || userDTO.getPassword().isBlank()) {
            model.addAttribute("error", "Username và Password không được để trống.");
            return "login";
        }

        Optional<User> userOptional = userService.authenticateUser(userDTO.getUsername(), userDTO.getPassword());

        if (userOptional.isPresent()) {
            User loggedInUser = userOptional.get();
            session.setAttribute("loggedInUser", loggedInUser); // Lưu user vào session

            // Kiểm tra vai trò để điều hướng
            if (loggedInUser.isRole()) { // Role là true (Admin)
                return "redirect:/dashboard";
            } else { // Role là false (User)
                return "redirect:/home";
            }
        } else {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
            return "login";
        }
    }

    // --- TRANG DASHBOARD CHO ADMIN (ĐÃ CẬP NHẬT) ---
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        // Kiểm tra đã đăng nhập VÀ có phải là admin không
        if (loggedInUser == null || !loggedInUser.isRole()) {
            return "redirect:/login";
        }
        return "dashboard";
    }

    // --- TRANG HOME CHO USER (MỚI) ---
    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            return "redirect:/login"; // Nếu chưa đăng nhập, về trang login
        }

        // Không cần kiểm tra vai trò ở đây vì cả admin và user đều có thể vào home
        // nhưng luồng đăng nhập đã tách họ ra.
        model.addAttribute("user", loggedInUser);
        return "home";
    }

    // --- ĐĂNG XUẤT (ĐÃ CẬP NHẬT) ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}