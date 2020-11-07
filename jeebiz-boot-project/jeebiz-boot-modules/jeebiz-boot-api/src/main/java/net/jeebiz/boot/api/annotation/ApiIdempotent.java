package net.jeebiz.boot.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface ApiIdempotent {

	/**
	 * 幂等名称：默认为空；
	 * type为Args时自动获取 @RequestMapping、@PostMapping、@GetMapping、@PutMapping、@DeleteMapping、@PatchMapping 的 value 值；
	 * type为Token时该值用于告诉拦截器取值的参数名
	 */
	String value() default "";
	
	/**
	 * 幂等方式
	 */
	ApiIdempotentType type() default ApiIdempotentType.ARGS;
	
	/**
	 * 是否启用 Spring Expression Language(SpEL) 表达式解析value值
	 */
	boolean spel() default false;
	
	/**
	 * 幂等过期时间，默认 200 毫秒，即：在此时间段内，对API进行幂等处理。
	 */
	long expireMillis() default 200;
	 
}
