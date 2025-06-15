package ra.edu.repository;

import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
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
}