package net.jeebiz.boot.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import net.jeebiz.boot.api.validation.StringDateValueValidator;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER,ElementType.TYPE_USE})
@Constraint(validatedBy = {StringDateValueValidator.class})
public @interface StringDateValue {

    String pattern() default "yyyy-MM-dd";

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
