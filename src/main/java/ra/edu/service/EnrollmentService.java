package ra.edu.service;

import ra.edu.entity.Course;
import ra.edu.entity.Enrollment;

import java.util.List;

public interface EnrollmentService {
    void save(Enrollment enrollment);
    boolean hasEnrolled(int studentId, int courseId);
    List<Integer> getRegisteredCourseIds(int studentId);

    long countTotalEnrollments();
    List<Object[]> countStudentByCourse();
    List<Object[]> top5CoursesByEnrollment();

    List<Enrollment> searchAdminEnrollments(String courseName, String status, int page, int pageSize);
    long countAdminEnrollments(String courseName, String status);
    boolean approveEnrollment(int enrollmentId);
    boolean denyEnrollment(int enrollmentId);

    boolean hasConfirmedEnrollmentForCourse(int courseId);

    List<Course> suggestCoursesByInstructor(int studentId);
    List<Course> suggestTopCourses(int limit);

    // EnrollmentService.java
    boolean canDeleteCourse(int courseId);
}