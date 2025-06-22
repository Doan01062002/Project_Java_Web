package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ra.edu.entity.Enrollment;
import ra.edu.repository.EnrollmentUserRepository;

import java.util.List;

@Service
public class EnrollmentUserServiceImpl implements EnrollmentUserService {

    @Autowired
    private EnrollmentUserRepository enrollmentUserRepository;

    @Override
    public List<Enrollment> getEnrollments(Integer studentId, int page, int pageSize) {
        return enrollmentUserRepository.findByStudentId(studentId, page, pageSize);
    }

    @Override
    public long getTotalEnrollments(Integer studentId) {
        return enrollmentUserRepository.countByStudentId(studentId);
    }

    @Override
    public List<Enrollment> searchEnrollments(Integer studentId,
                                              String keyword,
                                              String status,
                                              int page, int pageSize) {

        Enrollment.Status st = parseStatus(status);
        return enrollmentUserRepository.searchEnrollments(studentId, keyword, st, page, pageSize);
    }

    @Override
    public long countSearchEnrollments(Integer studentId,
                                       String keyword,
                                       String status) {

        Enrollment.Status st = parseStatus(status);
        return enrollmentUserRepository.countSearchEnrollments(studentId, keyword, st);
    }

    /* ---------- Tiện ích ---------- */
    private Enrollment.Status parseStatus(String status) {
        if (!StringUtils.hasText(status)) return null;
        try {
            return Enrollment.Status.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null; // nếu không match, coi như không lọc
        }
    }

    @Override
    public boolean cancelEnrollment(int enrollmentId, int studentId) {
        return enrollmentUserRepository.updateStatusIfWaiting(enrollmentId, studentId, Enrollment.Status.CANCEL);
    }
}