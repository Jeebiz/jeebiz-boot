package net.jeebiz.boot.api;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import net.jeebiz.boot.api.utils.DateUtils;

public class DateUtils_Test {


	public static void main(String[] args) {
		
		try {
			
			final String format = "yyyy-MM-dd HH:mm:ss";
			final String format2 = "yyyyMMdd HHmm";
			DateFormat dF = new SimpleDateFormat(format);
			long previous = DateUtils.getPreviousMillisEndWithMinute0or5(new Date());
			System.out.println(DateFormatUtils.format(new Date(), format) + " > " +  DateFormatUtils.format(previous, format2));
			
			long next = DateUtils.getNextMillisEndWithMinute0or5(new Date());
			System.out.println(DateFormatUtils.format(new Date(), format) + " > " +  DateFormatUtils.format(next, format2));
			

			long test2 = DateUtils.getNextMillisEndWithMinute0or5(dF.parse("2020-07-10 11:02:00"));
			System.out.println("2020-07-10 11:02:00 > " + DateFormatUtils.format(test2, format2));

			// 跨小时
			long test3 = DateUtils.getNextMillisEndWithMinute0or5(dF.parse("2020-07-10 12:59:59"));
			System.out.println("2020-07-10 12:59:59 > " + DateFormatUtils.format(test3, format2));
			
			// 跨天
			long test4 = DateUtils.getNextMillisEndWithMinute0or5(dF.parse("2020-07-10 23:59:59"));
			System.out.println("2020-07-10 23:59:59 > " + DateFormatUtils.format(test4, format2));

			// 跨闰月
			long test5 = DateUtils.getNextMillisEndWithMinute0or5(dF.parse("2020-02-29 23:55:59"));
			System.out.println("2020-02-29 23:55:59 > " + DateFormatUtils.format(test5, format2));

			// 跨闰年
			long test6 = DateUtils.getNextMillisEndWithMinute0or5(dF.parse("2019-12-31 23:55:59"));
			System.out.println("2019-12-31 23:55:59 > " + DateFormatUtils.format(test6, format2));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        		
	}
	
}
