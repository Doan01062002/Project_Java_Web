package ra.edu.repository;

import ra.edu.entity.Enrollment;

import java.util.List;

public interface EnrollmentRepository {
    void save(Enrollment enrollment);
    boolean hasEnrolled(int studentId, int courseId);
    List<Integer> getRegisteredCourseIds(int studentId);

    long countTotalEnrollments();
    List<Object[]> countStudentByCourse();
    List<Object[]> top5CoursesByEnrollment();
}