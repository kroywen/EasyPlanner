package com.thepegeekapps.easyplanner.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;

public class Utilities {
	
	public static final long INTERVAL_DAY = 24 * 60 * 60 * 1000;
	public static final String EEE_dd_LLL_yyyy = "EEE dd LLL yyyy";
	
	public static long getDayStart(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}
	
	public static long getDayEnd(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTimeInMillis();
	}
	
	public static boolean isEmpty(Collection<?> c) {
		return c == null || c.isEmpty();
	}
	
	public static int getDaysCount(long startTime, long endTime) {
		Calendar startDate = timeToMidnight(startTime);
		Calendar endDate = timeToMidnight(endTime);
		int daysCount = 0;
		while (startDate.before(endDate)) {
			startDate.add(Calendar.DAY_OF_MONTH, 1);
			daysCount++;
		}
		return daysCount;
	}
	
	public static Calendar timeToMidnight(long time){
	    Calendar cal = Calendar.getInstance();
	    cal.setTimeInMillis(time);      
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    return cal;
	}
	
	public static boolean isSameDay(long time1, long time2) {
		Calendar c1 = timeToMidnight(time1);
		Calendar c2 = timeToMidnight(time2);
		return c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String parseTime(long time, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
		return sdf.format(new Date(time));
	}
	
	public static long[][] generateWeekItems(long time) {
		Calendar c = timeToMidnight(time);
		while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			c.add(Calendar.DAY_OF_WEEK, -1);
		}
		long[][] items = new long[6][2];
		for (int i=0; i<6; i++) {
			items[i] = new long[2];
			items[i][0] = c.getTimeInMillis();

			c.add(Calendar.DAY_OF_MONTH, 1);
			if (i == 5) {
				items[i][1] = c.getTimeInMillis();
			}
		}
		return items;
	}
	
	public static long[][] generateMonthItems(long time) {
		Calendar c = timeToMidnight(time);
		while (c.get(Calendar.DAY_OF_MONTH) != 1) {
			c.add(Calendar.DAY_OF_WEEK, -1);
		}
		int daysCount = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		long[][] tmp = new long[daysCount][2];
		
		int month = c.get(Calendar.MONTH);
		int i = 0;
		while (c.get(Calendar.MONTH) == month) {
			tmp[i] = new long[2];
			tmp[i][0] = c.getTimeInMillis();
			
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				c.add(Calendar.DAY_OF_MONTH, 1);
				tmp[i][1] = c.getTimeInMillis();
			}
			
			c.add(Calendar.DAY_OF_MONTH, 1);
			i++;
		}
		
		long[][] items = new long[i][2];
		for (int j=0; j<i; j++) {
			items[j] = tmp[j];
		}
		return items; 
	}
	
	public static String addLeadingZero(int day) {
		return (day / 10 == 0) ? "0" + day : String.valueOf(day);
	}

}
