package io.hiwepy.boot.plugin.api.annotation;

import java.lang.annotation.*;


/**
 * 多个插件实现对象，指定默认的实现
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Primary {

}
