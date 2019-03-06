package net.jeebiz.boot.api.utils;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Constants {
	
	public static Marker marker = MarkerFactory.getMarker("Biz-Log");
	
	public static final String AUTHZ_FEATURE = "Authz-Feature";
	public static final String AUTHZ_FEATURE_OPT = "Authz-Feature-Opt";
	public static final String AUTHZ_LOGIN = "Authz-Login";
	public static final String AUTHZ_ROLE = "Authz-Role";
	public static final String AUTHZ_ROLE_PERMS = "Authz-Role-Perms";
	public static final String AUTHZ_USER = "Authz-User";
	public static final String AUTHZ_USER_PERMS = "Authz-User-Perms";
	//模板下载文件路径
	public static final String AUTHZ_USER_DOWNLOAD_TEMPLATE_URL = StringUtils.join(new String[] {"data", "template"}, File.separator);
	public static final String AUTHZ_USER_DOWNLOAD_TEMPLATE = "用户导入模板.xlsx";
}

