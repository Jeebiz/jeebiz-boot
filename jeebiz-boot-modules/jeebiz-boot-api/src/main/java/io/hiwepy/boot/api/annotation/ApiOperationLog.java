/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author <a href="https://github.com/wandl">wandl</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface ApiOperationLog {

    /**
     * 操作模块
     */
    String module() default "";

    /**
     * 业务名称
     */
    String business() default "";

    /**
     * 操作类型
     */
    BusinessType opt();

    /**
     * 是否马上处理
     */
    boolean immediate() default false;

}
