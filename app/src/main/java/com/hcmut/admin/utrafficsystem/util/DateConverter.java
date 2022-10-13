package com.hcmut.admin.utrafficsystem.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateConverter {
	public static final String DATE_FORMAT = "yyyy-MM-dd kk:mm:ss";
	public static final String TIME_ZONE = "Asia/Ho_Chi_Minh";
	
	
	public static String dateToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		formatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
		try {
			return formatter.format(date);
		} catch (Exception e) {
		}
		return "";
	}

	public static Date stringToDate(String dateString) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		formatter.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
		Date date = new Date();
		try {
			date = formatter.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Caculate time frame from prevTime to currTime
	 * 
	 * @param prevTime
	 * @param currTime
	 * @return time frame in seconds
	 */
	public static double caculateTimeFrame(String prevTime, String currTime) {
		return (double) (stringToDate(currTime).getTime() - stringToDate(prevTime).getTime()) / ((double) 1000);
	}

	/**
	 * Caculate time frame from prevTime to currTime
	 *
	 * @param prevTime
	 * @param currTime
	 * @return time frame in seconds
	 */
	public static double caculateTimeFrame(Date prevTime, Date currTime) {
		return (double) (currTime.getTime() - prevTime.getTime()) / ((double) 1000);
	}
}
