/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.setup.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class WebProfileAdvice {
 
    /**
	 * 应用到所有@RequestMapping注解方法，在其执行之前初始化数据绑定器
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	}

	/**
	 * 把值绑定到Model中，使全局@RequestMapping可以获取到该值
	 * @param model
	 */
	@ModelAttribute
	public void addAttributes(Model model) {
		//model.addAttribute("user", WebContext.getUser());
		//model.addAttribute("profile", WebContext.getUser());
	}

}
