package net.jeebiz.boot.api.utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarUtils {

	/**
     * Gets the current day of month.
     *
     * @return the current day of month.
     */
    public static int getDayOfMonth() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Gets the current month.
     *
     * @return the current month.
     */
    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    /**
     * Gets the current year.
     *
     * @return the current year.
     */
    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
    
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

	/**
	 * https://blog.csdn.net/bsr1983/article/details/84411990
	 * @param amount
	 * @return
	 */
	public static Timestamp fromUnixTime(int amount) {
		return new Timestamp(amount * 1000L);
	}
	
	public static int getDiffYear(int amount) {
		return getDiffYear(Locale.getDefault(), amount);
	}
	
	public static int getDiffYear(Locale locale, int amount) {
		return getDiffYear(TimeZone.getDefault(), locale, amount);
	}
	
	public static int getDiffYear(TimeZone zone, Locale locale, int amount) {
		
		Calendar birth = Calendar.getInstance();
		birth.setTimeInMillis(fromUnixTime(amount).getTime());
		
		// 当前时间
		Calendar now = Calendar.getInstance();
		
		/* 如果生日大于当前日期，则抛出异常：出生日期不能大于当前日期 */
		if (birth.after(now)) {
			throw new IllegalArgumentException("The birthday is after Now,It's unbelievable");
		}
		/* 取出当前年月日 */
		int yearNow = now.get(Calendar.YEAR);
		int monthNow = now.get(Calendar.MONTH) + 1;
		int dayNow = now.get(Calendar.DAY_OF_MONTH);
		/* 取出出生年月日 */
		int yearBirth = birth.get(Calendar.YEAR);
		int monthBirth = birth.get(Calendar.MONTH) + 1;
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

		System.out.println(CalendarUtils.fromUnixTime(400326583)); // 1982-09-08 17:49:43
		System.out.println(CalendarUtils.fromUnixTime(-28800));
		
		System.out.println(CalendarUtils.getDiffYear(400326583)); // 2002-09-07 00:00:00.000000
		System.out.println(CalendarUtils.getDiffYear(-28800));
		
	}
	
}
