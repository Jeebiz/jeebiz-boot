package io.hiwepy.boot.plugin.api.annotation;

import java.lang.annotation.*;

/**
 * 扩展点注解：用于标注某个功能扩展点的信息
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface ExtensionMapping {

	public String id() default "";

	public String title() default "";

	public String ver() default "1.0.0";

	public String desc() default "";

}
