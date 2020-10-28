/** 
 * Copyright (C) 2018 Jeebiz (http://jeebiz.net).
 * All Rights Reserved. 
 */
package net.jeebiz.boot.api.web;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import net.jeebiz.boot.api.ApiRestResponse;
import net.jeebiz.boot.api.utils.HttpStatus;

@ApiResponses({ 
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
	
	
	@ApiResponse(code = 10001, message = "认证失败", response = ApiRestResponse.class),
	@ApiResponse(code = 10002, message = "认证请求方法不支持.", response = ApiRestResponse.class),
	@ApiResponse(code = 10003, message = "登录失败次数操作最大限制，请输入验证码", response = ApiRestResponse.class),
	@ApiResponse(code = 10004, message = "验证码发送失败", response = ApiRestResponse.class),
	@ApiResponse(code = 10005, message = "请输入验证码", response = ApiRestResponse.class),
	@ApiResponse(code = 10006, message = "验证码已过期", response = ApiRestResponse.class),
	@ApiResponse(code = 10007, message = "验证码不可用", response = ApiRestResponse.class),
	@ApiResponse(code = 10008, message = "验证码错误", response = ApiRestResponse.class),
	@ApiResponse(code = 10009, message = "用户凭证已过期", response = ApiRestResponse.class),
	@ApiResponse(code = 10010, message = "用户名或密码错误", response = ApiRestResponse.class),
	@ApiResponse(code = 10011, message = "用户未注册", response = ApiRestResponse.class),
	@ApiResponse(code = 10012, message = "用户已注册", response = ApiRestResponse.class),
	@ApiResponse(code = 10013, message = "用户帐号不存在", response = ApiRestResponse.class),
	@ApiResponse(code = 10014, message = "用户帐号未启用", response = ApiRestResponse.class),
	@ApiResponse(code = 10015, message = "用户帐号已过期", response = ApiRestResponse.class),
	@ApiResponse(code = 10016, message = "用户帐号已被锁定", response = ApiRestResponse.class),
	@ApiResponse(code = 10017, message = "没有为用户指定角色", response = ApiRestResponse.class),
	@ApiResponse(code = 10021, message = "认证失败", response = ApiRestResponse.class),
	@ApiResponse(code = 10022, message = "Token缺失", response = ApiRestResponse.class),
	@ApiResponse(code = 10023, message = "Token已过期", response = ApiRestResponse.class),
	@ApiResponse(code = 10024, message = "Token已失效", response = ApiRestResponse.class),
	@ApiResponse(code = 10025, message = "Token错误", response = ApiRestResponse.class),
	@ApiResponse(code = 10026, message = "临时授权码过期", response = ApiRestResponse.class),
	@ApiResponse(code = 10027, message = "第三方授权服务端异常", response = ApiRestResponse.class),
	@ApiResponse(code = 10028, message = "临时授权码错误", response = ApiRestResponse.class),
	
})
public abstract class BaseApiController extends BaseMapperController {
	
}
