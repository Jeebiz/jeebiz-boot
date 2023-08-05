package io.hiwepy.boot.api.annotation;

import io.hiwepy.boot.api.validation.AllowedValuesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
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
