package net.jeebiz.boot.api.utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import hitool.core.lang3.time.TimestampUtils;

public class CalendarUtils extends hitool.core.lang3.time.CalendarUtils {

	/**
	 * https://blog.csdn.net/bsr1983/article/details/84411990
	 * @param amount
	 * @return
	 */
	public static Timestamp fromUnixTime(long amount) {
		return TimestampUtils.fromUnixTime(amount);
	}
	
	public static int getAgeFromUnixTime(long amount) {
		return TimestampUtils.getAgeFromUnixTime(Locale.getDefault(), amount);
	}
	
	public static int getAgeFromUnixTime(Locale locale, long amount) {
		return TimestampUtils.getAgeFromUnixTime(TimeZone.getDefault(), locale, amount);
	}
	
	public static int getAgeFromUnixTime(TimeZone zone, Locale locale, long amount) {
		return TimestampUtils.getAgeFromUnixTime(zone, locale, amount);
	}
	
	public static long getUnixTimeFromDate(Date date) {
		return TimestampUtils.getUnixTimeFromDate(TimeZone.getDefault(), date);
	}
	
	public static long getUnixTimeFromDate(TimeZone zone, Date date) {
		return TimestampUtils.getUnixTimeFromDate(zone, date);
	}
	
	public static void main(String[] args) {

		System.out.println(CalendarUtils.fromUnixTime(400326583)); // 1982-09-08 17:49:43
		System.out.println(CalendarUtils.fromUnixTime(-28800));
		
		System.out.println(CalendarUtils.getAgeFromUnixTime(400326583)); // 2002-09-07 00:00:00.000000
		System.out.println(CalendarUtils.getAgeFromUnixTime(-28800));
		
		System.out.println(CalendarUtils.getUnixTimeFromDate(new Date()));

	}
	
}
