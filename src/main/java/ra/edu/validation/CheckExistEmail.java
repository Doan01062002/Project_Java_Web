package ra.edu.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CheckExistEmailImpl.class)
@Target({
        ElementType.ANNOTATION_TYPE,
        ElementType.FIELD,
        ElementType.METHOD
})

@Retention(RetentionPolicy.RUNTIME)
public @interface CheckExistEmail {
    String message() default "Email đã tồn tại.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
