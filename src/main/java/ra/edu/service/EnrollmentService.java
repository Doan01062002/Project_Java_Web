package ra.edu.service;

import ra.edu.entity.Enrollment;

import java.util.List;

public interface EnrollmentService {
    void save(Enrollment enrollment);
    boolean hasEnrolled(int studentId, int courseId);
    List<Integer> getRegisteredCourseIds(int studentId);
}