/**
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved.
 */
package io.hiwepy.boot.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({DefaultSpringContextConfiguration.class, DefaultWebMvcConfiguration.class})
public @interface EnableExtrasConfiguration {

}
