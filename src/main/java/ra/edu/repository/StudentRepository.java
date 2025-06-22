package ra.edu.repository;

import ra.edu.entity.Student;

import java.util.List;

public interface StudentRepository {
    List<Student> findAll(int page, int size, String sortField, String sortDir, String keyword);
    int count(String keyword);
    Student findById(int id);
    void updateStatus(int id, boolean status);

    void update(Student student);
    void changePassword(int id, String newPassword);

    boolean checkPassword(int id, String password);

    boolean existsByEmailExceptId(String email, int exceptId);
    boolean existsByPhoneExceptId(String phone, int exceptId);
}