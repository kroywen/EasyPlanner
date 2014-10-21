package com.thepegeekapps.easyplanner.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Base64;

public class Utilities {
	
	public static final long INTERVAL_DAY = 24 * 60 * 60 * 1000;
	public static final String EEE_dd_LLL_yyyy = "EEE dd LLL yyyy";
	public static final String EEE_dd_LLL_yyyy_kk_mm_ss_Z = "EEE, dd LLL yyyy kk:mm:ss Z";
	public static final String dd_MM_yyyy = "dd-MM-yyyy";

	public static boolean isConnectionAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}
	
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
	
	public static long parseTime(String time, String pattern) 
		throws ParseException 
	{
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
		return sdf.parse(time).getTime();
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
	
	public static long[] generateTabletMonthItems(long time) {
		Calendar c = timeToMidnight(time);
		LinkedList<Long> list = new LinkedList<Long>();
		
		c.set(Calendar.DAY_OF_MONTH, 1);
		int month = c.get(Calendar.MONTH);
		while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			c.add(Calendar.DAY_OF_MONTH, -1);
			list.addFirst(c.getTimeInMillis());
		}
		
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, month);
		int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		while (c.get(Calendar.DAY_OF_MONTH) != lastDay) {
			list.add(c.getTimeInMillis());
			c.add(Calendar.DAY_OF_MONTH, 1);
		}
		
		while (c.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			list.addLast(c.getTimeInMillis());
			c.add(Calendar.DAY_OF_MONTH, 1);
		}
		list.addLast(c.getTimeInMillis());
		
		long[] items = new long[list.size()];
		for (int i=0; i<list.size(); i++) {
			items[i] = list.get(i);
		}
		
		return items;
	}
	
	public static String addLeadingZero(int day) {
		return (day / 10 == 0) ? "0" + day : String.valueOf(day);
	}
	
	public static String streamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
	public static final String getReadableFilesize(long bytes) {
		String s = " B";
		String size = bytes + s;
		
		while (bytes > 1024) {
			if (s.equals(" B"))	s = " KB";
			else if (s.equals(" KB")) s = " MB";
			else if (s.equals(" MB")) s = " GB";
			else if (s.equals(" GB")) s = " TB";
			
			size = (bytes / 1024) + "." + (bytes % 1024) + s;
			bytes = bytes / 1024;
		}
		
		return size;
	}
	
	public static String extractFilename(String path) {
		if (TextUtils.isEmpty(path)) {
			return "";
		}
		int start = path.lastIndexOf('/');
		if (start == -1) {
			return path;
		}
		return path.substring(start+1);
	}
	
	public static boolean copyStream(InputStream is, OutputStream os) {
		try {
			byte [] buffer = new byte[256];
			int bytesRead = 0;
			while((bytesRead = is.read(buffer)) != -1) {
			    os.write(buffer, 0, bytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				is.close();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public static boolean isTabletDevice(Context context) {
		boolean large = (context.getResources().getConfiguration().screenLayout & 
			Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE;
		boolean xlarge = (context.getResources().getConfiguration().screenLayout & 
				Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
		return large || xlarge;
	}
	
	public static String generateDeveloperPayload(String src) {
		return src != null ? Base64.encodeToString(src.getBytes(), 0) : null;
	}

}
