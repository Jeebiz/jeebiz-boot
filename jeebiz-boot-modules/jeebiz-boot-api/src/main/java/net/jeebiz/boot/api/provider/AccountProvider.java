/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.provider;

import java.util.Map;

import net.jeebiz.boot.api.dao.entities.BaseMap;

public interface AccountProvider {

	public boolean hasAccount(String username);
	
	/**
	 * 通过页面绑定的参数查询用户信息
	 */
	public BaseMap getAccount(Map<String, Object> data);
	
	public int resetPwd(Map<String, Object> data);
	
}
