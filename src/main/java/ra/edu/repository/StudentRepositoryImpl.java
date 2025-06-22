package ra.edu.repository;

import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.edu.entity.Student;

import java.util.List;

@Repository
@Transactional
public class StudentRepositoryImpl implements StudentRepository {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Student> findAll(int page, int size, String sortField, String sortDir, String keyword) {
        // Chỉ cho phép sắp xếp theo các trường hợp lệ
        String validSortField = switch (sortField) {
            case "id", "name", "email" -> sortField;
            default -> "id";
        };
        String validSortDir = "desc".equalsIgnoreCase(sortDir) ? "desc" : "asc";

        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Student s WHERE s.role = false";
            if (keyword != null && !keyword.trim().isEmpty()) {
                hql += " AND (lower(s.name) LIKE :kw OR lower(s.email) LIKE :kw OR str(s.id) LIKE :kw)";
            }
            hql += " ORDER BY s." + validSortField + " " + validSortDir;
            var query = session.createQuery(hql, Student.class);
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
            }
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            return query.list();
        }
    }

    @Override
    public int count(String keyword) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT COUNT(s.id) FROM Student s WHERE s.role = false";
            if (keyword != null && !keyword.trim().isEmpty()) {
                hql += " AND (lower(s.name) LIKE :kw OR lower(s.email) LIKE :kw OR str(s.id) LIKE :kw)";
            }
            var query = session.createQuery(hql, Long.class);
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
            }
            return query.uniqueResult().intValue();
        }
    }

    @Override
    public Student findById(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Student.class, id);
        }
    }

    @Override
    public void updateStatus(int id, boolean status) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Student student = session.get(Student.class, id);
            if (student != null) {
                student.setStatus(status);
                session.update(student);
            }
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Student student) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(student);
            session.getTransaction().commit();
        }
    }

    @Override
    public void changePassword(int id, String newPassword) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Student student = session.get(Student.class, id);
            if (student != null) {
                student.setPassword(newPassword);
                session.update(student);
            }
            session.getTransaction().commit();
        }
    }

    @Override
    public boolean checkPassword(int id, String password) {
        try (Session session = sessionFactory.openSession()) {
            Student student = session.get(Student.class, id);
            return student != null && student.getPassword().equals(password);
        }
    }

    @Override
    public boolean existsByEmailExceptId(String email, int exceptId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT COUNT(s.id) FROM Student s WHERE lower(s.email) = :email AND s.id <> :id";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("email", email.toLowerCase())
                    .setParameter("id", exceptId)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    @Override
    public boolean existsByPhoneExceptId(String phone, int exceptId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT COUNT(s.id) FROM Student s WHERE s.phone = :phone AND s.id <> :id";
            Long count = session.createQuery(hql, Long.class)
                    .setParameter("phone", phone)
                    .setParameter("id", exceptId)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }
}