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

    List<Enrollment> searchAdminEnrollments(String courseName, Enrollment.Status status, int page, int pageSize);
    long countAdminEnrollments(String courseName, Enrollment.Status status);

    boolean updateStatus(int enrollmentId, Enrollment.Status status);

    // Thêm vào interface EnrollmentRepository
    boolean hasConfirmedEnrollmentForCourse(int courseId);

    // EnrollmentRepository.java
    boolean canDeleteCourse(int courseId);
}