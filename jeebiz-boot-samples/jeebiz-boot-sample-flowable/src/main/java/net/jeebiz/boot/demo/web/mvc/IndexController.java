/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.demo.web.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 系统默认的重定向地址
 * https://www.thymeleaf.org/doc/articles/springmvcaccessdata.html
 */
@Controller
public class IndexController {

	/**
	 *  登录成功后的默认重定向地址：可重写返回的路径进行业务系统定制
	 */
	@RequestMapping("/index")
	public String index(HttpServletRequest request, HttpSession session, Model model) {
		session.setAttribute("mySessionAttribute", "someValue");
		return "html/index"; 
	}

}
