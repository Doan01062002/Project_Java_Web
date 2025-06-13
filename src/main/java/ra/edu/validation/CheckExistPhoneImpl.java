package ra.edu.validation;

import ra.edu.entity.Student;
import ra.edu.service.AuthenticationService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class CheckExistPhoneImpl implements ConstraintValidator<CheckExistPhone, String> {

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext constraintValidatorContext) {
        Student student = authenticationService.checkExistPhone(phone);
        if (student == null) {
            return true;
        }
        return false;
    }
}
