/**
 * 
 */
package net.jeebiz.boot.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * 操作日志注解
 * @author <a href="https://github.com/vindell">vindell</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface BusinessLog {
	
	/**
	 * 操作模块
	 */
	public String module() default "";
	
	/**
	 * 业务名称
	 */
	public String business() default "";
	
	/**
	 * 操作类型
	 */
	public BusinessType opt() ;
	
	/**
	 * 是否马上处理
	 */
	public boolean immediate() default false;
	
}
