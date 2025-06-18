package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.entity.Student;
import ra.edu.repository.StudentRepository;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public List<Student> findStudents(int page, int size, String sortField, String sortDir, String keyword) {
        return studentRepository.findAll(page, size, sortField, sortDir, keyword);
    }

    @Override
    public int countStudents(String keyword) {
        return studentRepository.count(keyword);
    }

    @Override
    public void lockOrUnlockStudent(int id, boolean status) {
        studentRepository.updateStatus(id, status);
    }

    @Override
    public Student getStudentById(int id) {
        return studentRepository.findById(id);
    }
}