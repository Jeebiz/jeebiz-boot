package io.hiwepy.boot.api.annotation;

import java.lang.annotation.*;

/**
 * Api模块注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface ApiModule {

    /**
     * 操作模块
     */
    String module() default "";

    /**
     * 业务名称
     */
    String business() default "";

}
