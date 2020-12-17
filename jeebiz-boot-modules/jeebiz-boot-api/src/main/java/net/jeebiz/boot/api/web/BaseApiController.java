/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.web;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.jeebiz.boot.api.ApiCodeValue;
import net.jeebiz.boot.api.ApiRestResponse;
import net.jeebiz.boot.api.utils.HttpStatus;

@ApiResponses({
	// 1、框架层
	@ApiResponse(code = HttpStatus.SC_BAD_REQUEST, message = "参数类型不匹配或格式不正确", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = "不允许访问（功能未授权）", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_FORBIDDEN, message = "服务器拒绝请求", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "请求地址不存在", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_METHOD_NOT_ALLOWED, message = "不支持的请求方法", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_NOT_ACCEPTABLE, message = "不匹配的媒体类型", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, message = "不支持的媒体类型", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_REQUEST_TOO_LONG, message = "请求实体过大", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "服务器内部错误", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_BAD_GATEWAY, message = "错误网关", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_SERVICE_UNAVAILABLE, message = "服务不可用", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_GATEWAY_TIMEOUT, message = "网关访问超时", response = ApiRestResponse.class),
	// 2、业务层
	@ApiResponse(code = ApiCodeValue.SC_FAIL, message = "失败", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHC_FAIL, message = "认证失败", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHC_METHOD_NOT_ALLOWED, message = "认证请求方法不支持.", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHC_OVER_RETRY_REMIND, message = "登录失败次数操作最大限制", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHC_CAPTCHA_REQUIRED, message = "验证码缺失", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHC_CAPTCHA_EXPIRED, message = "验证码已过期", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHC_CAPTCHA_INCORRECT, message = "验证码错误", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHC_ACCOUNT_NOT_FOUND, message = "用户帐号不存在", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHC_ACCOUNT_DISABLED, message = "用户帐号未启用", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHC_ACCOUNT_EXPIRED, message = "用户帐号已过期", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHC_ACCOUNT_LOCKED, message = "用户帐号已被锁定", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHC_CREDENTIALS_EXPIRED, message = "用户密码已过期", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHC_BAD_CREDENTIALS, message = "用户名或密码错误", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHZ_FAIL, message = "功能授权失败", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHZ_TOKEN_ISSUED, message = "Token签发失败", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHZ_TOKEN_REQUIRED, message = "Token缺失", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHZ_TOKEN_EXPIRED, message = "Token已过期", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHZ_TOKEN_INVALID, message = "Token已失效", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHZ_TOKEN_INCORRECT, message = "Token错误", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHZ_CODE_REQUIRED, message = "临时授权码缺失", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHZ_CODE_EXPIRED, message = "临时授权码过期", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHZ_CODE_INVALID, message = "临时授权码已失效", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHZ_CODE_INCORRECT, message = "临时授权码错误", response = ApiRestResponse.class),
	@ApiResponse(code = ApiCodeValue.SC_AUTHZ_THIRD_PARTY_SERVICE, message = "第三方授权服务端异常", response = ApiRestResponse.class)
	
})
public abstract class BaseApiController extends BaseMapperController {
	
}
