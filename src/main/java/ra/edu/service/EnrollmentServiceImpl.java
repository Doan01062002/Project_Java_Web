package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ra.edu.entity.Course;
import ra.edu.entity.Enrollment;
import ra.edu.repository.EnrollmentRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private CourseService courseService;

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

    @Override
    public long countTotalEnrollments() {
        return enrollmentRepository.countTotalEnrollments();
    }

    @Override
    public List<Object[]> countStudentByCourse() {
        return enrollmentRepository.countStudentByCourse();
    }

    @Override
    public List<Object[]> top5CoursesByEnrollment() {
        return enrollmentRepository.top5CoursesByEnrollment();
    }

    @Autowired
    private EnrollmentRepository enrollmentAdminRepository;

    private Enrollment.Status parseStatus(String status) {
        if (!StringUtils.hasText(status)) return null;
        try {
            return Enrollment.Status.valueOf(status.trim().toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Enrollment> searchAdminEnrollments(String courseName, String status, int page, int pageSize) {
        return enrollmentAdminRepository.searchAdminEnrollments(courseName, parseStatus(status), page, pageSize);
    }
    @Override
    public long countAdminEnrollments(String courseName, String status) {
        return enrollmentAdminRepository.countAdminEnrollments(courseName, parseStatus(status));
    }
    @Override
    public boolean approveEnrollment(int enrollmentId) {
        return enrollmentAdminRepository.updateStatus(enrollmentId, Enrollment.Status.CONFIRM);
    }
    @Override
    public boolean denyEnrollment(int enrollmentId) {
        return enrollmentAdminRepository.updateStatus(enrollmentId, Enrollment.Status.DENIED);
    }

    @Override
    public boolean hasConfirmedEnrollmentForCourse(int courseId) {
        return enrollmentRepository.hasConfirmedEnrollmentForCourse(courseId);
    }

    @Override
    public List<Course> suggestCoursesByInstructor(int studentId) {
        List<Enrollment> enrollments = enrollmentRepository.searchAdminEnrollments(null, Enrollment.Status.CONFIRM, 1, Integer.MAX_VALUE)
                .stream()
                .filter(e -> e.getStudent().getId() == studentId)
                .toList();
        Set<String> instructorNames = enrollments.stream()
                .map(e -> e.getCourse().getInstructor())
                .collect(Collectors.toSet());
        Set<Integer> registeredCourseIds = enrollments.stream()
                .map(e -> e.getCourse().getId())
                .collect(Collectors.toSet());
        List<Course> allCourses = courseService.findAll();
        return allCourses.stream()
                .filter(c -> instructorNames.contains(c.getInstructor()) && !registeredCourseIds.contains(c.getId()))
                .toList();
    }

    @Override
    public List<Course> suggestTopCourses(int limit) {
        List<Object[]> topCoursesData = enrollmentRepository.top5CoursesByEnrollment();
        return topCoursesData.stream()
                .map(obj -> (Course) obj[0])
                .limit(limit)
                .toList();
    }

    // EnrollmentServiceImpl.java
    @Override
    public boolean canDeleteCourse(int courseId) {
        return enrollmentRepository.canDeleteCourse(courseId);
    }
}