package io.hiwepy.boot.autoconfigure.validation.constraints;

import io.hiwepy.boot.autoconfigure.validation.constraintvalidators.StringDateValueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Constraint(validatedBy = {StringDateValueValidator.class})
public @interface StringDateValue {

    String pattern() default "yyyy-MM-dd";

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
