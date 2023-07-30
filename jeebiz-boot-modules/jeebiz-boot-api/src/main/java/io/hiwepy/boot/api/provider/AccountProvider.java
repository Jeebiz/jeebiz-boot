/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.hiwepy.boot.api.provider;

import io.hiwepy.boot.api.dao.entities.BaseMap;

import java.util.Map;

public interface AccountProvider {

    public boolean hasAccount(String username);

    /**
     * 通过页面绑定的参数查询用户信息
     */
    public BaseMap getAccount(Map<String, Object> data);

    public int resetPwd(Map<String, Object> data);

}
