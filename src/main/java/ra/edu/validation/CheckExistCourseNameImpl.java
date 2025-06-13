package ra.edu.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import ra.edu.entity.Course;
import ra.edu.service.CourseService;

public class CheckExistCourseNameImpl implements ConstraintValidator<CheckExistCourseName, String> {

    @Autowired
    private CourseService courseService;

    @Override
    public boolean isValid(String courseName, ConstraintValidatorContext constraintValidatorContext) {
        Course course = courseService.checkExistCourseName(courseName);
        if (course == null) {
            return true;
        }
        return false;
    }
}
