package com.example.project_java_web.service;

import com.example.project_java_web.dto.UserDTO;
import com.example.project_java_web.entity.User;
import com.example.project_java_web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User registerUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setName(userDTO.getName());
        user.setDob(userDTO.getDob());
        user.setEmail(userDTO.getEmail());
        user.setSex(userDTO.getSex());
        user.setPhone(userDTO.getPhone());
        user.setPassword(userDTO.getPassword()); // CẢNH BÁO: Vẫn chưa mã hóa mật khẩu!

        // Mặc định vai trò là User (student-0) khi đăng ký
        user.setRole(false);

        return userRepository.save(user);
    }

    @Override
    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Chỉ kiểm tra mật khẩu, việc kiểm tra vai trò sẽ do Controller xử lý
            if (password.equals(user.getPassword())) {
                return userOptional;
            }
        }
        return Optional.empty();
    }
}