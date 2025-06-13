package ra.edu.repository;

import ra.edu.dto.CourseDTO;
import ra.edu.entity.Course;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class CourseRepositoryImpl implements CourseRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Course> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Course").list();
        }
    }

    @Override
    public void addOrUpdateCourse(Course course) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(course);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteCourse(Course course) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(course);
            session.getTransaction().commit();
        }
    }

    @Override
    public Course getCourseById(int id) {
        // dung truoc tiep sessionFactory.getCurrentSession voi dk la tuong tác với khoá chính id
        return sessionFactory.getCurrentSession().get(Course.class, id);
    }@Override
    public Course checkExistCourseName(String name) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course WHERE name = :name", Course.class)
                    .setParameter("name", name)
                    .uniqueResult();
        }
    }

    @Override
    public Course findCourseByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course WHERE name LIKE :name", Course.class)
                    .setParameter("name", "%" + name + "%")
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }

    public List<Course> findAllByPage(int page, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Course", Course.class)
                    .setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .list();
        }
    }

    public long countTotalCourses() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT COUNT(c.id) FROM Course c", Long.class)
                    .uniqueResult();
        }
    }




}
