package ra.edu.dto;

import ra.edu.validation.CheckExistEmail;
import ra.edu.validation.CheckExistPhone;
import ra.edu.validation.CheckExistUserName;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudentDTO {

    private Integer id;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(max = 50, message = "Tên đăng nhập tối đa 50 ký tự")
    @CheckExistUserName
    private String username;

    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String name;

    @NotNull(message = "Ngày sinh không được để trống")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate dob;

    @NotBlank(message = "Email không được để trống")
    @Email(regexp = "^[\\w.-]+@[\\w.-]+\\.\\w{2,}$",message = "Email không đúng định dạng")
//    @Size(max = 100, message = "Email tối đa 100 ký tự")
    @CheckExistEmail
    private String email;

    @NotNull(message = "Giới tính không được để trống")
    private Boolean sex; // true = Nam, false = Nữ

    @NotBlank(message = "Số điện thoại không được để trống ")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    @CheckExistPhone
    private String phone;


    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 40, message = "Mật khẩu phải từ 6 đến 40 ký tự")
    private String password;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate createAt = LocalDate.now();

    @NotNull(message = "Vai trò không được để trống")
    private Boolean role = false; // false = Student, true = Admin

}
