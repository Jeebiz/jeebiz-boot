/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

import net.jeebiz.boot.api.dao.entities.PairModel;


public class PairValueListTypeHandler extends BaseTypeHandler<List<PairModel>> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, List<PairModel> list, JdbcType jdbcType)
			throws SQLException {
		ps.setString(i, JSONObject.toJSONString(list));
	}

	@Override
	public List<PairModel> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String rtString = rs.getString(columnName);
		if(StringUtils.hasText(rtString)) {
			return JSONObject.parseArray(rtString, PairModel.class);
		}
		return null;
	}

	@Override
	public List<PairModel> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String rtString = rs.getString(columnIndex);
		if(StringUtils.hasText(rtString)) {
			return JSONObject.parseArray(rtString, PairModel.class);
		}
		return null;
	}

	@Override
	public List<PairModel> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String rtString = cs.getString(columnIndex);
		if(StringUtils.hasText(rtString)) {
			return JSONObject.parseArray(rtString, PairModel.class);
		}
		return null;
	}


}
