package ra.edu.validation;

import ra.edu.entity.Student;
import ra.edu.service.AuthenticationService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class CheckExistEmailImpl implements ConstraintValidator<CheckExistEmail, String> {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        Student student = authenticationService.checkExistEmail(email);
        if (student == null) {
            return true;
        }
        return false;
    }
}
