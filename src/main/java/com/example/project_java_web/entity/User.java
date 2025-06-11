package com.example.project_java_web.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "users") // Tên bảng trong database
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @Column(length = 100, nullable = false)
    private String name;

    @Temporal(TemporalType.DATE)
    private Date dob;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean sex; // true: Nam-1, false: Nữ-0

    @Column(length = 20, unique = true)
    private String phone;

    @Column(length = 255, nullable = false)
    private String password;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_at", updatable = false)
    private Date createAt;

    @Column(nullable = false)
    private boolean role; // false: Student-0, true: Admin-1
}