package net.jeebiz.boot.api;

public class XHeaders {

	/**
	 * 客户端唯一请求ID
	 */
	public static final String X_REQUEST_ID = "X-Request-ID";
	
	/**
	 * JWT Token
	 */
	public static final String X_AUTHORIZATION = "X-Authorization";

	/**
	 * 经度
	 */
	public static final String X_LONGITUDE = "X-Longitude";
	/**
	 * 纬度
	 */
	public static final String X_LATITUDE = "X-Latitude";
	/**
	 * 最新定位省份
	 */
	public static final String X_PROVINCE = "X-Province";
	/**
	 * 最新定位城市
	 */
	public static final String X_CITY = "X-City";
	/**
	 * 最新定位区
	 */
	public static final String X_AREA = "X-Area";

	/**
	 * 国际化（zh_CN：简体中文、zh_TW：繁体中文、en_US：英语）
	 */
	public static final String X_LANGUAGE = "X-Language";
	/**
	 * 客户端时区
	 */
	public static final String X_TIMEZONE = "X-TimeZone";
	/**
	 * 账户UID
	 */
	public static final String X_UID = "X-Uid";
	/**
	 * 参数签名
	 */
	public static final String X_SIGN = "X-Sign";
	/**
	 * 客户端应用ID
	 */
	public static final String X_APP_ID = "X-APP-ID";
	/**
	 * 客户端应用渠道
	 */
	public static final String X_APP_CHANNEL = "X-APP-CHANNEL";
	/**
	 * 客户端应用版本号
	 */
	public static final String X_APP_VERSION = "X-APP-VERSION";
	/**
	 * 客户端设备唯一标识
	 */
	public static final String X_DEVICE_IMEI = "X-Device-IMEI";
	/**
	 * IOS 6+的设备唯一标识
	 */
	public static final String X_DEVICE_IDFA = "X-Device-IDFA";
	/**
	 * IOS 设备识别码
	 */
	public static final String X_DEVICE_OPENUDID = "X-Device-OPENUDID";
	/**
	 * Android id原值
	 */
	public static final String X_DEVICE_ANDROIDID = "X-Device-ANDROIDID";
	/**
	 * Android Q及更高版本的设备号
	 */
	public static final String X_DEVICE_OAID = "X-Device-OAID";
	/**
	 * 客户端设备Mac地址
	 */
	public static final String X_DEVICE_MAC = "X-Device-MAC";
	/**
	 * 客户端设备型号
	 */
	public static final String X_DEVICE_MODEL = "X-Device-Model";
	/**
	 * 客户端设备类型（ 2G、3G、4G、5G）
	 */
	public static final String X_DEVICE_NET_TYPE = "X-Device-NetType";
	/**
	 * 客户端设备系统版本
	 */
	public static final String X_DEVICE_OS = "X-OS";
	/**
	 * 接口请求来源（用于行为统计）
	 */
	public static final String X_REFERRER = "X-Referrer";

}

