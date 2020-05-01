/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.util.StringUtils;

public class StringListTypeHandler extends BaseTypeHandler<List<String>> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, List<String> list, JdbcType jdbcType)
			throws SQLException {
		if(list != null && !list.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			for (String s : list) {
				sb.append(s).append(",");
			}
			ps.setString(i, sb.toString().substring(0, sb.toString().length() - 1));
		} else {
			ps.setString(i, "");
		}
	}

	@Override
	public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String rtString = rs.getString(columnName);
		if(StringUtils.hasText(rtString)) {
			return Arrays.asList(rtString.split(","));
		}
		return null;
	}

	@Override
	public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String rtString = rs.getString(columnIndex);
		if(StringUtils.hasText(rtString)) {
			return Arrays.asList(rtString.split(","));
		}
		return null;
	}

	@Override
	public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String rtString = cs.getString(columnIndex);
		if(StringUtils.hasText(rtString)) {
			return Arrays.asList(rtString.split(","));
		}
		return null;
	}

}