package ra.edu.repository;

import ra.edu.entity.Student;

public interface AuthenticationRepository {
    void register(Student  student);

    Student checkExistUserName(String userName);

    Student checkExistEmail(String email);

    Student checkExistPhone(String phone);

    Student login (String email, String password);
}
