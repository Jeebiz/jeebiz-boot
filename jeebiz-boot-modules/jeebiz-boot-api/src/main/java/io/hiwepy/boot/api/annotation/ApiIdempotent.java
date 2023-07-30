package io.hiwepy.boot.api.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ApiIdempotent {

    /**
     * 幂等Key：默认为空；
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
     * 是否将参数作为幂等key的一部分
     */
    boolean withArgs() default false;

    /**
     * 幂等过期时间，默认 2000 毫秒，即：在此时间段内，对API进行幂等处理。
     */
    long expireMillis() default 2000;

    /**
     * 重试次数，默认0
     */
    int retryTimes() default 0;

    /**
     * 重试间隔时间，单位：ms，默认100
     */
    long retryInterval() default 100;

    /**
     * 是否自动进行解锁操作，默认：false, 等待key过期
     */
    boolean unlock() default false;

}
