package io.hiwepy.boot.api;

/**
 * @author wandl
 */
public final class ApiCodeValue {

	/**
	 * API Code 200（成功）
	 */
	public final static int SC_SUCCESS = 200;
	/**
	 * API Code 1000（失败）
	 */
	public final static int SC_FAIL = 1000;

	/**
	 * API Code 10001（账号登录失败）
	 */
	public final static int SC_AUTHC_FAIL = 10001;
	/**
	 * API Code 10002（认证请求方法不支持）
	 */
	public final static int SC_AUTHC_METHOD_NOT_ALLOWED = 10002;
	/**
	 * API Code 10003（登录失败次数操作最大限制）
	 */
	public final static int SC_AUTHC_OVER_RETRY_REMIND = 10003;
	/**
	 * API Code 10004（验证码缺失）
	 */
	public final static int SC_AUTHC_CAPTCHA_REQUIRED = 10004;
	/**
	 * API Code 10005（验证码已过期）
	 */
	public final static int SC_AUTHC_CAPTCHA_EXPIRED = 10005;
	/**
	 * API Code 10006（验证码错误）
	 */
	public final static int SC_AUTHC_CAPTCHA_INCORRECT = 10006;
	/**
	 * API Code 10007（用户帐号不存在）
	 */
	public final static int SC_AUTHC_ACCOUNT_NOT_FOUND = 10007;
	/**
	 * API Code 10008（用户帐号未启用）
	 */
	public final static int SC_AUTHC_ACCOUNT_DISABLED = 10008;
	/**
	 * API Code 10009（用户帐号已过期）
	 */
	public final static int SC_AUTHC_ACCOUNT_EXPIRED = 10009;
	/**
	 * API Code 10010（用户帐号被锁定）
	 */
	public final static int SC_AUTHC_ACCOUNT_LOCKED = 10010;
	/**
	 * API Code 10011（用户密码已过期）
	 */
	public final static int SC_AUTHC_CREDENTIALS_EXPIRED = 10011;
	/**
	 * API Code 10012（用户名或密码错误）
	 */
	public final static int SC_AUTHC_BAD_CREDENTIALS = 10012;
	/**
	 * API Code 10020（功能授权失败）
	 */
	public final static int SC_AUTHZ_FAIL = 10020;
	/**
	 * API Code 10021（Token签发失败）
	 */
	public final static int SC_AUTHZ_TOKEN_ISSUED = 10021;
	/**
	 * API Code 10022（Token缺失）
	 */
	public final static int SC_AUTHZ_TOKEN_REQUIRED = 10022;
	/**
	 * API Code 10023（Token已过期）
	 */
	public final static int SC_AUTHZ_TOKEN_EXPIRED = 10023;
	/**
	 * API Code 10024（Token已失效）
	 */
	public final static int SC_AUTHZ_TOKEN_INVALID = 10024;
	/**
	 * API Code 10025（Token错误）
	 */
	public final static int SC_AUTHZ_TOKEN_INCORRECT = 10025;
	/**
	 * API Code 10026（临时授权码缺失）
	 */
	public final static int SC_AUTHZ_CODE_REQUIRED = 10026;
	/**
	 * API Code 10027（临时授权码过期）
	 */
	public final static int SC_AUTHZ_CODE_EXPIRED = 10027;
	/**
	 * API Code 10028（临时授权码已失效）
	 */
	public final static int SC_AUTHZ_CODE_INVALID = 10028;
	/**
	 * API Code 10029（临时授权码错误）
	 */
	public final static int SC_AUTHZ_CODE_INCORRECT = 10029;
	/**
	 * API Code 10030（第三方授权服务端异常）
	 */
	public final static int SC_AUTHZ_THIRD_PARTY_SERVICE = 10030;

}
