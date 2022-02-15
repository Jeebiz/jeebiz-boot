package net.jeebiz.boot.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
	public String module() default "";

	/**
	 * 业务名称
	 */
	public String business() default "";

}