package io.hiwepy.boot.api.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils extends hitool.core.lang3.time.DateUtils {

	/**
	 * 获取前一个分钟值以0或者5结尾的时间点（单位：毫秒）
	 * [00,05,10,15,20,25,30,35,40,45,50,55]
	 * @return
	 */
	public static long getPreviousMillisEndWithMinute0or5(Date baseTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(baseTime);
		int minute = calendar.get(Calendar.MINUTE);
		if (minute < 5) {
			// 当前时间+1小时
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime().getTime();
		}
		
		int minus = minute % 5 < 5 ? minute % 5 : 0;

		calendar.add(Calendar.MINUTE, - minus);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime().getTime();

	}
	
	/**
	 * 获取下一个分钟值以0或者5结尾的时间点（单位：毫秒）
	 * [00,05,10,15,20,25,30,35,40,45,50,55]
	 * @return
	 */
	public static long getNextMillisEndWithMinute0or5(Date baseTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(baseTime);
		int minute = calendar.get(Calendar.MINUTE);
		if (minute < 55) {
			int mod = minute % 5;
			int add = mod < 5 ? 5 - mod : 0;
			calendar.add(Calendar.MINUTE, add);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime().getTime();
		}
		// 当前时间+1小时
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date endTime = DateUtils.addHours(calendar.getTime(), 1);
		return endTime.getTime();
	}
	
	/**
	 * 获取前一个分钟值以0结尾的时间点（单位：毫秒）
	 * [00,10,20,30,40,50]
	 * @return
	 */
	public static long getPreviousMillisEndWithMinute0(Date baseTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(baseTime);
		int minute = calendar.get(Calendar.MINUTE);
		if (minute < 10) {
			// 当前时间+1小时
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime().getTime();
		}
		
		int minus = minute % 10 == 0 ? 10 : minute % 10;
		
		calendar.add(Calendar.MINUTE, - minus);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime().getTime();

	}
	
	/**
	 * 获取下一个分钟值以0结尾的时间点（单位：毫秒）
	 * [00,10,20,30,40,50]
	 * @return
	 */
	public static long getNextMillisEndWithMinute0(Date baseTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(baseTime);
		int minute = calendar.get(Calendar.MINUTE);
		if (minute < 50) {
			int mod = minute % 10;
			int add = mod < 10 ? 10 - mod : 0;
			calendar.add(Calendar.MINUTE, add);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			return calendar.getTime().getTime();
		}
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date endTime = DateUtils.addHours(calendar.getTime(), 1); // 当前小时+1
		return endTime.getTime();
	}
	
}
