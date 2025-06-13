package ra.edu.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import ra.edu.validation.CheckExistCourseName;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {

    private Integer id;

    @NotBlank(message = "Tên khoá học không được để trống")
//    @CheckExistCourseName
    private String name;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng khoá học phải lớn hơn 0")
    @Max(value = 365, message = "Thời lượng không được vượt quá 365 ngày")
    private Integer duration;

    @NotBlank(message = "Tên giảng viên không được để trống")
    @Size(min = 3, max = 50, message = "Tên giảng viên phải từ 3 đến 50 ký tự")
    private String instructor;

    private LocalDate createAt = LocalDate.now();

    private String image;

    // Không cần validate ở đây, vì file có thể là optional
    private MultipartFile imageFile;
}
