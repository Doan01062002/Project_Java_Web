package ra.edu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "student")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dob;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean sex; // true: Nam, false: Nữ

    @Column(length = 20, unique = true)
    private String phone;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(name = "create_at", nullable = false)
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate createAt = LocalDate.now();

    @Column(nullable = false)
    private Boolean role = false; //false : Student
                                  // true : Admin

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments;
}