package net.jeebiz.boot.api.utils;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarUtils {

	/**
	 * 判断当前时间距离第二天凌晨的秒数
	 * 
	 * @return 返回值单位为[s:秒]
	 */
	public static long getSecondsNextEarlyMorning() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
	}

	public static int getYearSince1970(int amount) {
		return getYearSince1970(Locale.getDefault(), amount);
	}
	
	public static int getYearSince1970(Locale locale, int amount) {
		return getYearSince1970(TimeZone.getDefault(), locale, amount);
	}
	
	public static int getYearSince1970(TimeZone zone, Locale locale, int amount) {

		// 1970-07-01 00:00:00 作为时间的对比点
		Calendar birth = Calendar.getInstance(zone, locale);
		birth.set(Calendar.YEAR, 1970);
		birth.set(Calendar.MONTH, 7);
		birth.set(Calendar.DAY_OF_MONTH, 1);
		birth.set(Calendar.HOUR_OF_DAY, 0);
		birth.set(Calendar.SECOND, 0);
		birth.set(Calendar.MINUTE, 0);
		birth.set(Calendar.MILLISECOND, 0);
		// 增加指定秒（可能是负数）
		birth.add(Calendar.MINUTE, amount);

		// 当前时间
		Calendar now = Calendar.getInstance();
		/* 如果生日大于当前日期，则抛出异常：出生日期不能大于当前日期 */
		if (birth.after(now)) {
			throw new IllegalArgumentException("The birthday is after Now,It's unbelievable");
		}
		/* 取出当前年月日 */
		int yearNow = now.get(Calendar.YEAR);
		int monthNow = now.get(Calendar.MONTH);
		int dayNow = now.get(Calendar.DAY_OF_MONTH);
		/* 取出出生年月日 */
		int yearBirth = birth.get(Calendar.YEAR);
		int monthBirth = birth.get(Calendar.MONTH);
		int dayBirth = birth.get(Calendar.DAY_OF_MONTH);
		/* 大概年龄是当前年减去出生年 */
		int age = yearNow - yearBirth;
		/* 如果出当前月小与出生月，或者当前月等于出生月但是当前日小于出生日，那么年龄age就减一岁 */
		if (monthNow < monthBirth || (monthNow == monthBirth && dayNow < dayBirth)) {
			age--;
		}
		return age;
	}

	public static void main(String[] args) {
		
		System.out.println(CalendarUtils.getYearSince1970(-28800));
		
	}
	
}
