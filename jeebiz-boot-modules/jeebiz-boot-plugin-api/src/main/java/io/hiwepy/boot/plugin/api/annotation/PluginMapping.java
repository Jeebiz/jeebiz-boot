package io.hiwepy.boot.plugin.api.annotation;

import java.lang.annotation.*;

/**
 * 插件注解：用于标记插件的信息
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface PluginMapping {
	
	public String title() default "";
	
	public String detail() default "";
	
}
