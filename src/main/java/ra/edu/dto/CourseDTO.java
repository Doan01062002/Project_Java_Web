package ra.edu.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {

    private Integer id;

    @NotBlank(message = "Tên khoá học không được để trống")
    private String name;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng khoá học phải lớn hơn 0")
    @Max(value = 365, message = "Thời lượng không được vượt quá 365 ngày")
    private Integer duration;

    @Size(min = 3, max = 50, message = "Tên giảng viên phải từ 3 đến 50 ký tự")
    private String instructor;

    private LocalDate createAt = LocalDate.now();

    private String image;

    private MultipartFile imageFile;
}
