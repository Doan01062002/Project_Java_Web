package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.dto.StudentDTO;
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

    private StudentDTO convertStudentToStudentDTO(Student student) {
        StudentDTO studentdto = new StudentDTO();
        studentdto.setId(student.getId());
        studentdto.setUsername(student.getUsername());
        studentdto.setName(student.getName());
        studentdto.setDob(student.getDob());
        studentdto.setEmail(student.getEmail());
        studentdto.setSex(student.getSex());
        studentdto.setPhone(student.getPhone());
        studentdto.setPassword(student.getPassword());
        studentdto.setCreateAt(student.getCreateAt());
        studentdto.setRole(student.getRole());
        studentdto.setStatus(student.isStatus());
        return studentdto;
    }

    private Student convertStudentDTOToStudent(StudentDTO studentDTO) {
        Student student = new Student();
        student.setId(studentDTO.getId());
        student.setUsername(studentDTO.getUsername());
        student.setName(studentDTO.getName());
        student.setDob(studentDTO.getDob());
        student.setEmail(studentDTO.getEmail());
        student.setSex(studentDTO.getSex());
        student.setPhone(studentDTO.getPhone());
        student.setPassword(studentDTO.getPassword());
        student.setCreateAt(studentDTO.getCreateAt());
        student.setRole(studentDTO.getRole());
        student.setStatus(studentDTO.isStatus());
        return student;
    }

    @Override
    public void updateStudent(Student student) {
        studentRepository.update(student);
    }

    @Override
    public void changePassword(int id, String newPassword) {
        studentRepository.changePassword(id, newPassword);
    }

    @Override
    public boolean checkPassword(int studentId, String password) {
        Student student = studentRepository.findById(studentId);
        return student != null && student.getPassword().equals(password);
    }

    @Override
    public boolean isEmailDuplicate(String email, int exceptId) {
        return studentRepository.existsByEmailExceptId(email, exceptId);
    }

    @Override
    public boolean isPhoneDuplicate(String phone, int exceptId) {
        return studentRepository.existsByPhoneExceptId(phone, exceptId);
    }
}