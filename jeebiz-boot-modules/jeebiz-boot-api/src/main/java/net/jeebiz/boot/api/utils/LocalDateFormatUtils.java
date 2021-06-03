package net.jeebiz.boot.api.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateFormatUtils {
	
	public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	public static final String YYYYMMDD = "yyyy-MM-dd";

	private LocalDateFormatUtils() {
	}

	public static String formatYMD(LocalDateTime date) {
		return format(date, YYYYMMDD);
	}

	public static String formatYMDHMS(LocalDateTime date) {
		return format(date, YYYYMMDDHHMMSS);
	}

	public static LocalDateTime formatYMD(String date) {
		return format(date, YYYYMMDD);
	}

	public static LocalDateTime formatYMDHMS(String date) {
		return format(date, YYYYMMDDHHMMSS);
	}

	public static String format(LocalDateTime date, String pattern) {
		return date == null ? null : getStringFormat(date, pattern);
	}

	public static LocalDateTime format(String date, String pattern) {
		return date == null ? null : getDateFormat(date, pattern);
	}

	/**
	 * 日期解析字符串
	 * 
	 * @return String
	 */
	public static String getStringFormat(LocalDateTime date, String pattern) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
		return date.format(format);
	}

	/**
	 * 字符串解析日期
	 * 
	 * @return LocalDateTime
	 */
	public static LocalDateTime getDateFormat(String date, String pattern) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
		return LocalDateTime.parse(date, format);
	}

}
