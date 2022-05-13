package io.hiwepy.boot.api;

public class XHeaders {

	/**
	 * 客户端唯一请求ID
	 */
	public static final String X_REQUEST_ID = "X-Request-Id";

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
	 * 国家地区编码：http://doc.chacuo.net/iso-3166-1
	 */
	public static final String X_REGION = "X-Region";
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
	 * 参数签名
	 */
	public static final String X_SIGN = "X-Sign";
	/**
	 * 客户端应用ID
	 */
	public static final String X_APP_ID = "X-App-ID";
	/**
	 * 客户端应用渠道
	 */
	public static final String X_APP_CHANNEL = "X-App-Channel";
	/**
	 * 客户端应用版本号
	 */
	public static final String X_APP_VERSION = "X-App-Version";
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

	/**
	 * 账户UID
	 */
	public static final String X_UID = "X-Uid";
	/**
	 * 用户名
	 */
	public static final String X_UNAME = "X-Uname";
	/**
	 * 用户Key：用户业务表中的唯一ID
	 */
	public static final String X_UKEY = "X-Ukey";
	/**
	 * 用户Code：用户业务表中的唯一编码
	 */
	public static final String X_UCODE = "X-Ucode";
	/**
	 * 角色ID（角色表Id）
	 */
	public static final String X_RID = "X-Rid";
	/**
	 * 角色Key：角色业务表中的唯一ID
	 */
	public static final String X_RKEY = "X-Rkey";
	/**
	 * 角色Code：角色业务表中的唯一编码
	 */
	public static final String X_RCODE = "X-Rcode";


}

