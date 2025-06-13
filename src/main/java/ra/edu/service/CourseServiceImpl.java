package ra.edu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.edu.dto.CourseDTO;
import ra.edu.entity.Course;
import ra.edu.repository.CourseRepository;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;


    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> findAllByPage(int page, int pageSize) {
        return courseRepository.findAllByPage(page, pageSize);
    }

    @Override
    public long countTotalCourses() {
        return courseRepository.countTotalCourses();
    }


    @Override
    public void addOrUpdateCourse(CourseDTO courseDTO) {
        Course course = convertCourseDTOToCourse(courseDTO);
        courseRepository.addOrUpdateCourse(course);
    }

    @Override
    public Course checkExistCourseName(String name) {
        return courseRepository.checkExistCourseName(name);
    }

    @Override
    public boolean isCourseNameDuplicate(String name) {
        return courseRepository.checkExistCourseName(name) != null;
    }

    @Override
    public boolean isCourseNameDuplicate(String name, int id) {
        Course existing = courseRepository.checkExistCourseName(name);
        return existing != null && existing.getId() != id;
    }



    @Override
    public Course getCourseById(int id) {
        return courseRepository.getCourseById(id);
    }

    @Override
    public Course findCourseByName(String name){
        return courseRepository.findCourseByName(name);
    }

    @Override
    public void deleteCourse(Course course) {
        courseRepository.deleteCourse(course);
    }

    private Course convertCourseDTOToCourse(CourseDTO courseDTO) {
        Course course = new Course();
        course.setId(courseDTO.getId());
        course.setName(courseDTO.getName());
        course.setDuration(courseDTO.getDuration());
        course.setInstructor(courseDTO.getInstructor());
        course.setCreateAt(course.getCreateAt());
        course.setImage(courseDTO.getImage());
        return course;
    }
}
