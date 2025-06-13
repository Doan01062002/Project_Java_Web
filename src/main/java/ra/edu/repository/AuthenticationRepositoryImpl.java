package ra.edu.repository;

import ra.edu.entity.Student;
import jakarta.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class AuthenticationRepositoryImpl implements AuthenticationRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void register(Student student) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(student);
            session.getTransaction().commit();
        }
    }

    @Override
    public Student checkExistUserName(String userName) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Student where username = :userName", Student.class)
                    .setParameter("userName", userName)
                    .uniqueResult();
        }
    }

    @Override
    public Student checkExistEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Student where email = :email", Student.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    @Override
    public Student checkExistPhone(String phone) {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("from Student where phone = :phone",Student.class)
                    .setParameter("phone", phone)
                    .uniqueResult();
        }
    }
    @Override
    public Student login(String email, String password) {
        try (Session session = sessionFactory.openSession()) {
            Student student = session.createQuery("from Student where email = :email and password = :password", Student.class)
                    .setParameter("email", email.trim().toLowerCase())
                    .setParameter("password", password)
                    .uniqueResult();
            System.out.println("Truy vấn login: email=" + email.trim().toLowerCase() + ", result=" + (student != null ? student.getEmail() : "null"));
            return student;
        }
    }

}
