package net.jeebiz.boot.api;

public class XHeaders {

	/**
	 * JWT Token
	 */
	public static final String X_AUTHORIZATION_HEADER = "X-Authorization";
	/**
	 * 经度
	 */
	public static final String X_LONGITUDE_HEADER = "X-Longitude";
	/**
	 * 纬度
	 */
	public static final String X_LATITUDE_HEADER = "X-Latitude";
	/**
	 * 最新定位省份
	 */
	public static final String X_PROVINCE_HEADER = "X-Province";
	/**
	 * 最新定位城市
	 */
	public static final String X_CITY_HEADER = "X-City";
	/**
	 * 最新定位区
	 */
	public static final String X_AREA_HEADER = "X-Area";
	/**
	 * 国际化（zh_CN：简体中文、zh_TW：繁体中文、en_US：英语）
	 */
	public static final String X_LANGUAGE_HEADER = "X-Language";
	/**
	 * 账户UID
	 */
	public static final String X_UID_HEADER = "X-Uid";
	/**
	 * 参数签名
	 */
	public static final String X_SIGN_HEADER = "X-Sign";
	/**
	 * 客户端应用ID
	 */
	public static final String X_APP_ID_HEADER = "X-APP-ID";
	/**
	 * 客户端应用渠道
	 */
	public static final String X_APP_CHANNEL_HEADER = "X-APP-CHANNEL";
	/**
	 * 客户端应用版本号
	 */
	public static final String X_APP_VERSION_HEADER = "X-APP-VERSION";
	/**
	 * 接口请求来源（用于行为统计）
	 */
	public static final String X_REFERRER_HEADER = "X-Referrer";
	/**
	 * 客户端设备唯一标识
	 */
	public static final String X_DEVICE_IMEI_HEADER = "X-Device-IMEI";
	/**
	 * 客户端设备型号
	 */
	public static final String X_DEVICE_MODEL_HEADER = "X-Device-Model";

}

