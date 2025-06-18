package ra.edu.repository;

import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import ra.edu.entity.Enrollment;

import java.util.List;

@Repository
@Transactional
public class EnrollmentUserRepositoryImpl implements EnrollmentUserRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    @SuppressWarnings("unchecked")
    public List<Enrollment> findByStudentId(Integer studentId, int page, int pageSize) {
        Session session = sessionFactory.getCurrentSession();
        String hql = """
                     from Enrollment e
                     join fetch e.course      
                     where e.student.id = :sid
                     order by e.registeredAt desc
                     """;
        return session.createQuery(hql)
                .setParameter("sid", studentId)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize)
                .list();
    }

    @Override
    public long countByStudentId(Integer studentId) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "select count(e.id) from Enrollment e where e.student.id = :sid";
        return (long) session.createQuery(hql)
                .setParameter("sid", studentId)
                .uniqueResult();
    }

    @Override
    public List<Enrollment> searchEnrollments(Integer studentId,
                                              String keyword,
                                              Enrollment.Status status,
                                              int page, int pageSize) {

        Session session = sessionFactory.getCurrentSession();

        StringBuilder hql = new StringBuilder("""
                from Enrollment e
                join fetch e.course c
                where e.student.id = :sid
                """);

        if (StringUtils.hasText(keyword)) {
            hql.append(" and lower(c.name) like :kw");
        }
        if (status != null) {
            hql.append(" and e.status = :st");
        }

        // Đảm bảo sắp xếp theo trạng thái trước, sau đó tới ngày đăng ký
        hql.append(" order by e.status asc, e.registeredAt desc");

        var query = session.createQuery(hql.toString(), Enrollment.class)
                .setParameter("sid", studentId)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize);

        if (StringUtils.hasText(keyword)) {
            query.setParameter("kw", "%" + keyword.toLowerCase().trim() + "%");
        }
        if (status != null) {
            query.setParameter("st", status);
        }

        return query.list();
    }

    @Override
    public long countSearchEnrollments(Integer studentId,
                                       String keyword,
                                       Enrollment.Status status) {

        Session session = sessionFactory.getCurrentSession();

        StringBuilder hql = new StringBuilder("""
                select count(e.id)
                from Enrollment e
                join e.course c
                where e.student.id = :sid
                """);

        if (StringUtils.hasText(keyword)) {
            hql.append(" and lower(c.name) like :kw");
        }
        if (status != null) {
            hql.append(" and e.status = :st");
        }

        var query = session.createQuery(hql.toString(), Long.class)
                .setParameter("sid", studentId);

        if (StringUtils.hasText(keyword)) {
            query.setParameter("kw", "%" + keyword.toLowerCase().trim() + "%");
        }
        if (status != null) {
            query.setParameter("st", status);
        }

        return query.uniqueResult();
    }
}