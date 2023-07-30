package io.hiwepy.boot.api.annotation;

import io.hiwepy.boot.api.validation.PhoneValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Constraint(validatedBy = {PhoneValueValidator.class})
public @interface Phone {

    String lang() default "CN";

    String value() default "";

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
