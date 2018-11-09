/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.authz.rbac0.web.mvc;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.biz.utils.SubjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import springfox.documentation.annotations.ApiIgnore;


/**
 * 权限管理：系统首页
 */
@Api(tags = "权限管理：系统首页（Ok）")
@Controller
public class AuthzIndexController {
	
	@ApiIgnore
	@RequestMapping(value="callback")
    public String callback(HttpServletRequest request, Model model) {
    	/**
    	 * 如果用户已登录，直接转发到首页
    	 */
    	if(SubjectUtils.isAuthenticated()){
    		return "redirect:/index";
    	}
    	return "redirect:/authz/login/stateful";
    }
	
	/**
	 * JWT是否过期的访问方法：处理逻辑已经在过滤器中实现，这里只负责输出有效情况下的信息
	 * @param request
	 * @return
	 */
	@ApiIgnore
	@RequestMapping(value = "expiry")
	@ResponseBody
	public Object expiry(HttpServletRequest request) {
		// 响应成功状态信息
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("status", "success");
		data.put("message", "JWT within validity period.");
		return data;
	}
 
}
