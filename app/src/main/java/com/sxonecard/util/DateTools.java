package com.sxonecard.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 
 * <p>
 * Title: 日期的操作类
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2010
 * </p>
 * <p>
 * Company: Inc.
 * </p>
 * 
 * @author Ant
 * @version 1.0
 */
public class DateTools {

	public static Calendar getCalendar() {
		return Calendar.getInstance(getTimeZone(), getLocale());
	}

	/**
	 * 得到一个默认的本地语言
	 *
	 * @return
	 */
	public static Locale getLocale() {
		return Locale.getDefault();
	}

	/**
	 * 得到有一个默认的本地时区
	 *
	 * @return
	 */
	public static TimeZone getTimeZone() {
		return TimeZone.getDefault();
	}

	/**
	 * @param s
	 * @param e
	 * @return boolean
	 * @throws
	 * @Title: compareDate
	 * @Description: TODO(日期比较，如果s>=e 返回true 否则返回false)
	 * @author luguosui
	 */
	public static boolean compareDate(String s, String e) {
		if (formatDate(s) == null || formatDate(e) == null) {
			return false;
		}
		return formatDate(s).getTime() >= formatDate(e).getTime();
	}

	/**
	 * 格式化日期
	 *
	 * @return
	 */
	public static Date formatDate(String date) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			return fmt.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static Calendar fromDate(String date){
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date convert = fmt.parse(date);
			Calendar c = Calendar.getInstance();
			c.setTime(convert);
			return c;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getCurrent() {
		SimpleDateFormat sdfTime = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return sdfTime.format(new Date());
	}
}