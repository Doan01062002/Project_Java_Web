package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.entity.Enrollment;
import ra.edu.repository.EnrollmentRepository;

import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Override
    public void save(Enrollment enrollment) {
        enrollmentRepository.save(enrollment);
    }

    @Override
    public boolean hasEnrolled(int studentId, int courseId) {
        return enrollmentRepository.hasEnrolled(studentId, courseId);
    }

    @Override
    public List<Integer> getRegisteredCourseIds(int studentId) {
        return enrollmentRepository.getRegisteredCourseIds(studentId);
    }
}