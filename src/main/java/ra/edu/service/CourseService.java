package ra.edu.service;

import ra.edu.dto.CourseDTO;
import ra.edu.entity.Course;

import java.util.List;

public interface CourseService {

    List<Course> findAll();

    void addOrUpdateCourse(CourseDTO courseDTO);

    Course checkExistCourseName(String name); // hàm check trùng tên khoá học

    boolean isCourseNameDuplicate(String name); // Dùng cho thêm
    boolean isCourseNameDuplicate(String name, int id); // Dùng cho sửa

    Course getCourseById(int id);

    Course findCourseByName(String name); // hàm tìm kiếm khoá học thoe tên

    void deleteCourse(Course course);

    List<Course> findAllByPage(int page, int pageSize);
    long countTotalCourses();

}
