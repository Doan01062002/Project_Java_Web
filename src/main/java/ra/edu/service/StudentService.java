package ra.edu.service;

import ra.edu.entity.Student;

import java.util.List;

public interface StudentService {
    List<Student> findStudents(int page, int size, String sortField, String sortDir, String keyword);
    int countStudents(String keyword);
    void lockOrUnlockStudent(int id, boolean status);
    Student getStudentById(int id);
}