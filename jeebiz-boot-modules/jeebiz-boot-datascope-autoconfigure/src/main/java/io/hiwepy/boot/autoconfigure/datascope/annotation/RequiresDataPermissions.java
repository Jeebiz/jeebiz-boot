package io.hiwepy.boot.autoconfigure.datascope.annotation;

import io.hiwepy.boot.autoconfigure.datascope.DataType;
import io.hiwepy.boot.autoconfigure.datascope.RequiresDataPermissionsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * jsr303 数据权限校验注解
 *
 * @author wandl
 * @version 1.0
 * @since 2024.10.09
 */
@Documented
@Constraint( validatedBy = {RequiresDataPermissionsValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresDataPermissions {

    /**
     * 数据类型
     * @return the data type
     */
    DataType dataType();

    /**
     * Message string.
     *
     * @return the string
     */
    String message() default "数据未授权";

    /**
     * 校验组
     *
     * @return the class [ ]
     */
    Class<?>[] groups() default {};

    /**
     * Payload class [ ].
     *
     * @return the class [ ]
     */
    Class<? extends Payload>[] payload() default {};

}
