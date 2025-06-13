package ra.edu.repository;

import ra.edu.entity.Course;

import java.util.List;

public interface CourseRepository {

    List<Course> findAllByPage(int page, int pageSize);
    long countTotalCourses();


    List<Course> findAll();

    void addOrUpdateCourse(Course course);

    Course checkExistCourseName(String name); // hàm check trùng tên khoá học

    Course getCourseById(int id);

    Course findCourseByName(String name); // hàm tìm kiếm khoá học thoe tên

    void deleteCourse(Course course);
}
