package net.jeebiz.boot.api.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * http://blog.csdn.net/caoshuming_500/article/details/20952329
 * https://www.cnblogs.com/wang1001/p/9605761.html
 * 
 * @author <a href="https://github.com/hiwepy">hiwepy</a>
 */
@Slf4j
public class WebUtils extends org.springframework.web.util.WebUtils {

	private static String[] xheaders = new String[] { "X-Forwarded-For", "x-forwarded-for" };
	private static String[] headers = new String[] { "Cdn-Src-Ip", "Proxy-Client-IP", "WL-Proxy-Client-IP", "X-Real-IP",
			"HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR" };
	private static String LOCAL_HOST = "localhost";
	private static String LOCAL_IP6 = "0:0:0:0:0:0:0:1";
	private static String LOCAL_IP = "127.0.0.1";
	private static String UNKNOWN = "unknown";

	/**
	 * 获取请求客户端IP地址，支持代理服务器
	 * 
	 * @param request {@link HttpServletRequest} 对象
	 * @return IP地址
	 */
	public static String getRemoteAddr(HttpServletRequest request) {

		// 1、获取客户端IP地址，支持代理服务器
		String remoteAddr = UNKNOWN;
		for (String xheader : xheaders) {
			remoteAddr = request.getHeader(xheader);
			log.debug(" {} : {} ", xheader, remoteAddr);
			if (StringUtils.hasText(remoteAddr) && !UNKNOWN.equalsIgnoreCase(remoteAddr)) {
				// 多次反向代理后会有多个ip值，第一个ip才是真实ip
				if (remoteAddr.indexOf(",") != -1) {
					remoteAddr = remoteAddr.split(",")[0];
				}
				break;
			}
		}
		if (!StringUtils.hasText(remoteAddr) || UNKNOWN.equalsIgnoreCase(remoteAddr)) {
			for (String header : headers) {

				remoteAddr = request.getHeader(header);
				log.debug(" {} : {} ", header, remoteAddr);

				if (StringUtils.hasText(remoteAddr) && !UNKNOWN.equalsIgnoreCase(remoteAddr)) {
					break;
				}
			}
		}
		// 2、没有取得特定标记的值
		if (!StringUtils.hasText(remoteAddr) || UNKNOWN.equalsIgnoreCase(remoteAddr)) {
			remoteAddr = request.getRemoteAddr();
		}
		// 3、判断是否localhost访问
		if (LOCAL_HOST.equals(remoteAddr) || LOCAL_IP6.equals(remoteAddr)) {
			remoteAddr = LOCAL_IP;
		}

		return remoteAddr;
	}

	public static boolean internalIp(String ip) {
		byte[] addr = textToNumericFormatV4(ip);
		return internalIp(addr) || "127.0.0.1".equals(ip);
	}

	private static boolean internalIp(byte[] addr) {
		if (Objects.isNull(addr) || addr.length < 2) {
			return true;
		}
		final byte b0 = addr[0];
		final byte b1 = addr[1];
		// 10.x.x.x/8
		final byte SECTION_1 = 0x0A;
		// 172.16.x.x/12
		final byte SECTION_2 = (byte) 0xAC;
		final byte SECTION_3 = (byte) 0x10;
		final byte SECTION_4 = (byte) 0x1F;
		// 192.168.x.x/16
		final byte SECTION_5 = (byte) 0xC0;
		final byte SECTION_6 = (byte) 0xA8;
		switch (b0) {
		case SECTION_1:
			return true;
		case SECTION_2:
			if (b1 >= SECTION_3 && b1 <= SECTION_4) {
				return true;
			}
		case SECTION_5:
			switch (b1) {
			case SECTION_6:
				return true;
			}
		default:
			return false;
		}
	}

	/**
	 * 将IPv4地址转换成字节
	 * 
	 * @param text IPv4地址
	 * @return byte 字节
	 */
	public static byte[] textToNumericFormatV4(String text) {
		if (text.length() == 0) {
			return null;
		}

		byte[] bytes = new byte[4];
		String[] elements = text.split("\\.", -1);
		try {
			long l;
			int i;
			switch (elements.length) {
			case 1:
				l = Long.parseLong(elements[0]);
				if ((l < 0L) || (l > 4294967295L)) {
					return null;
				}
				bytes[0] = (byte) (int) (l >> 24 & 0xFF);
				bytes[1] = (byte) (int) ((l & 0xFFFFFF) >> 16 & 0xFF);
				bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
				bytes[3] = (byte) (int) (l & 0xFF);
				break;
			case 2:
				l = Integer.parseInt(elements[0]);
				if ((l < 0L) || (l > 255L)) {
					return null;
				}
				bytes[0] = (byte) (int) (l & 0xFF);
				l = Integer.parseInt(elements[1]);
				if ((l < 0L) || (l > 16777215L)) {
					return null;
				}
				bytes[1] = (byte) (int) (l >> 16 & 0xFF);
				bytes[2] = (byte) (int) ((l & 0xFFFF) >> 8 & 0xFF);
				bytes[3] = (byte) (int) (l & 0xFF);
				break;
			case 3:
				for (i = 0; i < 2; ++i) {
					l = Integer.parseInt(elements[i]);
					if ((l < 0L) || (l > 255L)) {
						return null;
					}
					bytes[i] = (byte) (int) (l & 0xFF);
				}
				l = Integer.parseInt(elements[2]);
				if ((l < 0L) || (l > 65535L)) {
					return null;
				}
				bytes[2] = (byte) (int) (l >> 8 & 0xFF);
				bytes[3] = (byte) (int) (l & 0xFF);
				break;
			case 4:
				for (i = 0; i < 4; ++i) {
					l = Integer.parseInt(elements[i]);
					if ((l < 0L) || (l > 255L)) {
						return null;
					}
					bytes[i] = (byte) (int) (l & 0xFF);
				}
				break;
			default:
				return null;
			}
		} catch (NumberFormatException e) {
			return null;
		}
		return bytes;
	}

	public static String getHostIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
		}
		return "127.0.0.1";
	}

	public static String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		}
		return "未知";
	}
	
	public static boolean isSameSegment(HttpServletRequest request) {
		String localIp = getHostIp();
		String remoteIp = getRemoteAddr(request);
		log.info("localIp:{},remoteIp:{} url:{}", localIp, remoteIp, request.getRequestURI());
		int mask = getIpV4Value("255.255.255.0");
		boolean flag = (mask & getIpV4Value(localIp)) == (mask & getIpV4Value(remoteIp));
		return flag;
	}

	public static int getIpV4Value(String ipOrMask) {
		byte[] addr = getIpV4Bytes(ipOrMask);
		int address1 = addr[3] & 0xFF;
		address1 |= ((addr[2] << 8) & 0xFF00);
		address1 |= ((addr[1] << 16) & 0xFF0000);
		address1 |= ((addr[0] << 24) & 0xFF000000);
		return address1;
	}

	public static byte[] getIpV4Bytes(String ipOrMask) {
		try {

			String[] addrs = ipOrMask.split("\\.");
			int length = addrs.length;
			byte[] addr = new byte[length];
			for (int index = 0; index < length; index++) {
				addr[index] = (byte) (Integer.parseInt(addrs[index]) & 0xff);
			}
			return addr;
		} catch (Exception e) {
		}
		return new byte[4];
	}

	/**
	 * 获得请求的客户端信息【ip,port,name】
	 * 
	 * @param request {@link HttpServletRequest} 对象
	 * @return 客户端信息[ip,port,name]
	 */
	public static String[] getRemoteInfo(HttpServletRequest request) {
		if (request == null) {
			return new String[] { "", "", "" };
		}
		return new String[] { getRemoteAddr(request), request.getRemotePort() + "", request.getRemoteHost() };
	}

}
