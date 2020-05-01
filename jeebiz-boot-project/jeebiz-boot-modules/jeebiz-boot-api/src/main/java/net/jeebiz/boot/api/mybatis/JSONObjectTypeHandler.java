/** 
 * Copyright (C) 2019 杭州知乎者也科技有限公司 (http://knowway.cn).
 * All Rights Reserved.
 */
package com.knowway.cloud.api.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

public class JSONObjectTypeHandler extends BaseTypeHandler<JSONObject> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, JSONObject parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, parameter.toJSONString());
	}

	@Override
	public JSONObject getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String rtString = rs.getString(columnName);
		if(StringUtils.hasText(rtString)) {
			return JSONObject.parseObject(rtString);
		}
		return null;
	}

	@Override
	public JSONObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String rtString = rs.getString(columnIndex);
		if(StringUtils.hasText(rtString)) {
			return JSONObject.parseObject(rtString);
		}
		return null;
	}

	@Override
	public JSONObject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String rtString = cs.getString(columnIndex);
		if(StringUtils.hasText(rtString)) {
			return JSONObject.parseObject(rtString);
		}
		return null;
	}

}
