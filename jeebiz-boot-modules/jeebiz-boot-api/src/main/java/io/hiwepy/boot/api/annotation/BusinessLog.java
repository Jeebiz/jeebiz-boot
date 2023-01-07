/** 
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved. 
 */
package io.hiwepy.boot.api.annotation;

import java.lang.annotation.*;



/**
 * 操作日志注解
 * @author <a href="https://github.com/wandl">wandl</a>
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
