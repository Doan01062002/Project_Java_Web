package ra.edu.service;

import ra.edu.dto.StudentDTO;
import ra.edu.entity.Student;

public interface AuthenticationService {
    void register(StudentDTO studentDTO);

    Student checkExistUserName(String userName);

    Student checkExistEmail(String email);

    Student checkExistPhone(String phone);

    StudentDTO login (String email, String password);

}
