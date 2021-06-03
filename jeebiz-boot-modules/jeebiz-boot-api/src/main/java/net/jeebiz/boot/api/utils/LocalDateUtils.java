package net.jeebiz.boot.api.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

public class LocalDateUtils {

	public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	public static final String YYYYMMDD = "yyyy-MM-dd";

	private LocalDateUtils() {
	}

	public static String formatYMD(LocalDateTime date) {
		return format(date, YYYYMMDD);
	}

	public static String formatYMDHMS(LocalDateTime date) {
		return format(date, YYYYMMDDHHMMSS);
	}

	public static String format(LocalDateTime date, String format) {
		return LocalDateFormatUtils.format(date, format);
	}

	/**
	 * @return 当天开始时间 00:00
	 */
	public static LocalDateTime firstTimeOfDay() {
		return LocalDate.now().atStartOfDay();
	}

	/**
	 * @return 当天最后时间 23:59:59
	 */
	public static LocalDateTime lastTimeOfDay() {
		return LocalDate.now().atTime(23, 59, 59);
	}

	/**
	 * @return 明天日期
	 */
	public static LocalDate getTomorrow() {
		return LocalDate.now().plusDays(1);
	}

	/**
	 * @return 本月第一天
	 */
	public static LocalDate firstDayOfThisMonth() {
		return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
	}

	/**
	 * @return 本月最后一天
	 */
	public static LocalDate lastDayOfMonth() {
		return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
	}

	/**
     * 判断当前日期是否在两个日期期间内
     * @param before
     * @param after
     * @return true or false
     */
    public static boolean twoDatePeriod(LocalDateTime before,LocalDateTime after){
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(before) && now.isBefore(after);
    }
}
