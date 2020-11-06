package net.jeebiz.boot.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Idempotent {

	/**
	 * 幂等名称：支持 Spring Expression Language(SpEL) 表达式，默认为空；
	 * type为Args时自动获取 @RequestMapping、@PostMapping、@GetMapping、@PutMapping、@DeleteMapping、@PatchMapping 的 value 值；
	 * type为Token时该值用于告诉拦截器取值的参数名
	 */
	String value() default "";
	
	/**
	 * 幂等方式
	 */
	IdempotentType type() default IdempotentType.ARGS;
	
	/**
	 * 幂等过期时间，即：在此时间段内，对API进行幂等处理。
	 */
	long expireMillis();
	 
}
