package io.hiwepy.boot.autoconfigure.validation.constraints;


import io.hiwepy.boot.autoconfigure.validation.constraintvalidators.AllowedValuesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Constraint(validatedBy = AllowedValuesValidator.class)
public @interface AllowableValues {

    String message() default "invalid values";

    String allows() default "";

    boolean nullable() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
