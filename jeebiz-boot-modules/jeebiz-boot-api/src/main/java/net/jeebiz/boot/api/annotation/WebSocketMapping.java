package net.jeebiz.boot.api.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface WebSocketMapping {

    String value() default "";

}
