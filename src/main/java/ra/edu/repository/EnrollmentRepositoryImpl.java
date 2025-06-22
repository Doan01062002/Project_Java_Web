package ra.edu.repository;

import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ra.edu.entity.Enrollment;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
public class EnrollmentRepositoryImpl implements EnrollmentRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(Enrollment enrollment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(enrollment);
            session.getTransaction().commit();
        }
    }

    @Override
    public boolean hasEnrolled(int studentId, int courseId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery("SELECT COUNT(e.id) FROM Enrollment e WHERE e.student.id = :sid AND e.course.id = :cid", Long.class)
                    .setParameter("sid", studentId)
                    .setParameter("cid", courseId)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    @Override
    public List<Integer> getRegisteredCourseIds(int studentId) {
        try (Session session = sessionFactory.openSession()) {
            List<Enrollment> enrollments = session.createQuery("FROM Enrollment e WHERE e.student.id = :sid", Enrollment.class)
                    .setParameter("sid", studentId)
                    .list();
            return enrollments.stream().map(e -> e.getCourse().getId()).collect(Collectors.toList());
        }
    }

    @Override
    public long countTotalEnrollments() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "SELECT COUNT(e.id) FROM Enrollment e WHERE e.status = :status", Long.class)
                    .setParameter("status", Enrollment.Status.CONFIRM)
                    .uniqueResult();
        }
    }

    @Override
    public List<Object[]> countStudentByCourse() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "SELECT e.course, COUNT(e.id) " +
                                    "FROM Enrollment e WHERE e.status = :status " +
                                    "GROUP BY e.course", Object[].class)
                    .setParameter("status", Enrollment.Status.CONFIRM)
                    .list();
        }
    }

    @Override
    public List<Object[]> top5CoursesByEnrollment() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "SELECT e.course, COUNT(e.id) " +
                                    "FROM Enrollment e WHERE e.status = :status " +
                                    "GROUP BY e.course " +
                                    "ORDER BY COUNT(e.id) DESC", Object[].class)
                    .setParameter("status", Enrollment.Status.CONFIRM)
                    .setMaxResults(5)
                    .list();
        }
    }

    @Override
    public List<Enrollment> searchAdminEnrollments(String courseName, Enrollment.Status status, int page, int pageSize) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder("""
        from Enrollment e join fetch e.student s join fetch e.course c where 1=1
    """);
        if (StringUtils.hasText(courseName)) {
            hql.append(" and lower(c.name) like :kw");
        }
        if (status != null) {
            hql.append(" and e.status = :st");
        }
        hql.append(" order by e.registeredAt desc");
        var query = session.createQuery(hql.toString(), Enrollment.class)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize);
        if (StringUtils.hasText(courseName)) {
            query.setParameter("kw", "%" + courseName.toLowerCase().trim() + "%");
        }
        if (status != null) {
            query.setParameter("st", status);
        }
        return query.list();
    }

    @Override
    public long countAdminEnrollments(String courseName, Enrollment.Status status) {
        Session session = sessionFactory.getCurrentSession();
        StringBuilder hql = new StringBuilder("""
        select count(e.id) from Enrollment e join e.course c where 1=1
    """);
        if (StringUtils.hasText(courseName)) {
            hql.append(" and lower(c.name) like :kw");
        }
        if (status != null) {
            hql.append(" and e.status = :st");
        }
        var query = session.createQuery(hql.toString(), Long.class);
        if (StringUtils.hasText(courseName)) {
            query.setParameter("kw", "%" + courseName.toLowerCase().trim() + "%");
        }
        if (status != null) {
            query.setParameter("st", status);
        }
        return query.uniqueResult();
    }

    @Override
    public boolean updateStatus(int enrollmentId, Enrollment.Status status) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "update Enrollment e set e.status = :st where e.id = :eid";
        int updated = session.createQuery(hql)
                .setParameter("st", status)
                .setParameter("eid", enrollmentId)
                .executeUpdate();
        return updated > 0;
    }

    @Override
    public boolean hasConfirmedEnrollmentForCourse(int courseId) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(e.id) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = :status", Long.class)
                    .setParameter("courseId", courseId)
                    .setParameter("status", Enrollment.Status.CONFIRM)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    @Override
    public boolean canDeleteCourse(int courseId) {
        try (Session session = sessionFactory.openSession()) {
            // Đếm số lượng enrollments không phải CANCEL với courseId
            Long count = session.createQuery(
                            "SELECT COUNT(e.id) FROM Enrollment e WHERE e.course.id = :courseId AND e.status IN (:statuses)",
                            Long.class)
                    .setParameter("courseId", courseId)
                    .setParameterList("statuses", List.of(
                            ra.edu.entity.Enrollment.Status.WAITING,
                            ra.edu.entity.Enrollment.Status.DENIED,
                            ra.edu.entity.Enrollment.Status.CONFIRM
                    ))
                    .uniqueResult();
            // Nếu count == 0 tức là KHÔNG có WAITING/DENIED/CONFIRM => có thể xóa
            return count == 0;
        }
    }
}