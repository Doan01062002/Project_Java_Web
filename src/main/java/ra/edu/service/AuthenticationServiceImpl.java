package ra.edu.service;

import ra.edu.dto.StudentDTO;
import ra.edu.entity.Student;
import ra.edu.repository.AuthenticationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Override
    public void register(StudentDTO studentDTO) {
        Student student = convertStudentDTOToStudent(studentDTO);
        authenticationRepository.register(student);
    }

    @Override
    public Student checkExistUserName(String userName) {
        return authenticationRepository.checkExistUserName(userName);
    }

    @Override
    public Student checkExistEmail(String email) {
        return authenticationRepository.checkExistEmail(email);
    }

    @Override
    public Student checkExistPhone(String phone) {
        return authenticationRepository.checkExistPhone(phone);
    }

    @Override
    public StudentDTO login(String email, String password) {
        Student student = authenticationRepository.login(email, password);
        System.out.println("Kết quả login: " + (student != null ? student.getEmail() : "null"));
        if (student != null) {
            return convertStudentToStudentDTO(student);
        }
        return null;
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
        return student;
    }
}
