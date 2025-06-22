package ra.edu.repository;

import ra.edu.entity.Enrollment;

import java.util.List;

public interface EnrollmentUserRepository {
    /** Lấy danh sách enrollment của 1 sinh viên, có phân trang */
    List<Enrollment> findByStudentId(Integer studentId, int page, int pageSize);

    /** Đếm tổng số bản ghi (để tính totalPages) */
    long countByStudentId(Integer studentId);


    List<Enrollment> searchEnrollments(Integer studentId, String keyword, Enrollment.Status status,
                                       int page, int pageSize);

    long countSearchEnrollments(Integer studentId, String keyword,
                                Enrollment.Status status);

    boolean updateStatusIfWaiting(int enrollmentId, int studentId, Enrollment.Status status);
}