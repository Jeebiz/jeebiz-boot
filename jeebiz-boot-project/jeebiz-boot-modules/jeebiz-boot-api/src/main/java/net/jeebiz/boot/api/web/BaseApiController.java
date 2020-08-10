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
	@ApiResponse(code = 0, message = "认证成功", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_CREATED, message = "已创建", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_UNAUTHORIZED, message = "请求要求身份验证", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_FORBIDDEN, message = "权限不足", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_NOT_FOUND, message = "请求资源不存在", response = ApiRestResponse.class),
	@ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR, message = "服务器内部异常", response = ApiRestResponse.class),
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
	@ApiResponse(code = 10110, message = "不允许访问（功能未授权）", response = ApiRestResponse.class),
	@ApiResponse(code = 10111, message = "请求失败", response = ApiRestResponse.class),
	@ApiResponse(code = 10112, message = "数据为空", response = ApiRestResponse.class),
	@ApiResponse(code = 10113, message = "参数类型不匹配", response = ApiRestResponse.class),
	@ApiResponse(code = 10114, message = "缺少矩阵变量", response = ApiRestResponse.class),
	@ApiResponse(code = 10115, message = "缺少URI模板变量", response = ApiRestResponse.class),
	@ApiResponse(code = 10116, message = "缺少Cookie变量", response = ApiRestResponse.class),
	@ApiResponse(code = 10117, message = "缺少请求头", response = ApiRestResponse.class),
	@ApiResponse(code = 10118, message = "缺少参数", response = ApiRestResponse.class),
	@ApiResponse(code = 10119, message = "缺少请求对象", response = ApiRestResponse.class),
	@ApiResponse(code = 10120, message = "参数规则不满足", response = ApiRestResponse.class),
	@ApiResponse(code = 10121, message = "参数绑定错误", response = ApiRestResponse.class),
	@ApiResponse(code = 10122, message = "参数解析错误", response = ApiRestResponse.class),
	@ApiResponse(code = 10123, message = "参数验证失败", response = ApiRestResponse.class),
	@ApiResponse(code = 10201, message = "服务器：运行时异常", response = ApiRestResponse.class),
	@ApiResponse(code = 10202, message = "服务器：空值异常", response = ApiRestResponse.class),
	@ApiResponse(code = 10203, message = "服务器：数据类型转换异常", response = ApiRestResponse.class),
	@ApiResponse(code = 10204, message = "服务器：IO异常", response = ApiRestResponse.class),
	@ApiResponse(code = 10205, message = "服务器：未知方法异常", response = ApiRestResponse.class),
	@ApiResponse(code = 10206, message = "服务器：非法参数异常", response = ApiRestResponse.class),
	@ApiResponse(code = 10207, message = "服务器：数组越界异常", response = ApiRestResponse.class),
	@ApiResponse(code = 10208, message = "服务器：网络异常", response = ApiRestResponse.class)
})
public abstract class BaseApiController extends BaseMapperController {
	
}
