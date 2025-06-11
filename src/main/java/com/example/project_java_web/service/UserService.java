package com.example.project_java_web.service;

import com.example.project_java_web.dto.UserDTO;
import com.example.project_java_web.entity.User;

import java.util.Optional;

public interface UserService {
    // Đăng ký người dùng mới
    User registerUser(UserDTO userDTO);

    // Xác thực người dùng (cho cả user và admin)
    Optional<User> authenticateUser(String username, String password);
}