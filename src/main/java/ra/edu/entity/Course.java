package ra.edu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "course")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer duration;

    @Column(length = 100, nullable = false)
    private String instructor;

    @Column(name = "create_at", nullable = false)
    private LocalDate createAt = LocalDate.now();

    @Column(length = 500)
    private String image;

    // Quan hệ 1-n với bảng Enrollment
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments;
}
