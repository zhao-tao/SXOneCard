package com.sxonecard.http;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by pc on 2017-04-27.
 */

public class DateUtil
{
    /**
     *
     * @param map
     * @return
     * @description 将Map里的String 类型转换成 Date类型
     * @version currentVersion
     */
    public static Map<String, Object> convertStringMapToDateTimeMap(
            Map<String, Object> map) throws ParseException {
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            if (key.toLowerCase().contains("time")) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
                String dateStr = (String) map.get(key);
                map.put(key, formatter.parse(dateStr));
            }
        }
        return map;
    }

    public static long getDateIntervalWithHours(String startDate, String endDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        Date date1, date2;
        try {
            date1 = formatter.parse(startDate);
            date2 = formatter.parse(endDate);
            cal1.setTime(date1);
            cal2.setTime(date2);
            return (cal2.getTimeInMillis() - cal1.getTimeInMillis())
                    / (1000 * 60 * 60);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }


    /**
     *
     * @return
     * @description 得到当前时间 2015-11-20 14:41:19
     * @version currentVersion
     */
    public static String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = formatter.format(new Date());
        return createTime;
    }

    public static String formatDateToSting(String dateString,
                                           String formatString) {
        SimpleDateFormat in = new SimpleDateFormat(formatString);
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return in.format(out.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }

    }

    public static String formatStringoDate(String dateString,
                                           String formatString) {
        SimpleDateFormat in = new SimpleDateFormat(formatString);
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd");

        try {
            return out.format(in.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }

    }

    /**
     *
     * @return
     * @description 得到当前时间 2015-11-20
     * @version currentVersion
     */
    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String createTime = formatter.format(new Date());
        return createTime;
    }

    /**
     *
     * @return
     * @description
     * @version currentVersion 返回格式 2015-11-20T13:06:30.000+08:00
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = formatter.format(new Date());
        return createTime.substring(0, 10) + "T"
                + createTime.substring(11, createTime.length()) + ".000+08:00";
    }

    public static String getCurrentMillisecondTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS");
        String createTime = formatter.format(new Date());
        return createTime.substring(0, 10) + " "
                + createTime.substring(11, createTime.length());
    }


    /**
     *
     * @param date
     * @return
     * @description 得到年份
     * @version currentVersion
     */
    public static String getYear(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        String year = formatter.format(date);
        return year;
    }

    /**
     *
     * @param date
     * @return
     * @description 得到月份
     * @version currentVersion
     */
    public static String getMonth(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("M");
        String month = formatter.format(date);
        return month;
    }

    /**
     *
     * @param startDate
     *            开始日期字符串 yyyy-MM-dd
     * @param afterDay
     *            结束日期字符串 yyyy-MM-dd
     * @return
     * @Description: 得到給定日期之后的N天日期字符串
     * @version currentVersion
     */
    public static String getDateAfter(String startDate, int afterDay)
            throws RuntimeException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        Date date;
        try {
            date = formatter.parse(startDate);
            cal.setTime(date);
            cal.add(Calendar.DATE, afterDay);
            String createTime = formatter.format(cal.getTime());
            return createTime;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public static String getDateAfterHHMMDDSSSSS(String startDate, int afterDay)
            throws RuntimeException {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS");
        Calendar cal = Calendar.getInstance();
        Date date;
        try {
            date = formatter.parse(startDate);
            cal.setTime(date);
            cal.add(Calendar.DATE, afterDay);
            String createTime = formatter.format(cal.getTime());
            return createTime;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     *
     * @param startDate
     *            yyyy-MM-dd
     * @param afterMonth
     *            返回值为yyyy-MM格式的
     * @return
     * @description
     * @version currentVersion
     */
    public static String getMonthAfter(String startDate, int afterMonth) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        Calendar cal = Calendar.getInstance();
        Date date;
        try {
            date = formatter.parse(startDate);
            cal.setTime(date);
            cal.add(Calendar.MONTH, afterMonth);
            String createTime = formatter.format(cal.getTime());
            return createTime;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getHourAfter(String startTime, int afterHour) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date date;
        try {
            date = formatter.parse(startTime);
            cal.setTime(date);
            cal.add(Calendar.HOUR, afterHour);
            String createTime = formatter.format(cal.getTime());
            return createTime;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMinutesAfter(String startTime, int minutes) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        Date date;
        try {
            date = formatter.parse(startTime);
            cal.setTime(date);
            cal.add(Calendar.MINUTE, minutes);
            String createTime = formatter.format(cal.getTime());
            return createTime;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMinutesAfterHHmmSSsss(String startTime, int minutes) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS");
        Calendar cal = Calendar.getInstance();
        Date date;
        try {
            date = formatter.parse(startTime);
            cal.setTime(date);
            cal.add(Calendar.MINUTE, minutes);
            String createTime = formatter.format(cal.getTime());
            return createTime;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param dateStr
     *            日期字符串
     * @param originalFormat
     *            原始日期格式
     * @param targetFormat
     *            目标日期格式
     * @return
     * @throws ParseException
     *             转换一个日期到相应的格式
     */
    public static String parseDateFormat(String dateStr, String originalFormat,
                                         String targetFormat) {
        SimpleDateFormat originalformatter = new SimpleDateFormat(
                originalFormat);
        SimpleDateFormat targetformatter = new SimpleDateFormat(targetFormat);
        Date date;
        try {
            date = originalformatter.parse(dateStr);
            return targetformatter.format(date);
        } catch (ParseException e) {
            return null;
        }

    }

    /**
     *
     * @param startDate
     *            yyyy-MM-dd
     * @param endDate
     *            yyyy-MM-dd
     * @throws RuntimeException
     */

    public static int getDateInterval(String startDate, String endDate)
            throws RuntimeException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date1, date2;
        try {
            date1 = formatter.parse(startDate);
            date2 = formatter.parse(endDate);
            return (int) ((date2.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }

    }

    public static long getDateIntervalWithHour(String startDate, String endDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        Date date1, date2;
        try {
            date1 = formatter.parse(startDate);
            date2 = formatter.parse(endDate);
            cal1.setTime(date1);
            cal2.setTime(date2);
            return (cal2.getTimeInMillis() - cal1.getTimeInMillis())
                    / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param startTime
     * @param endTime
     * @return
     * @description 得到两个时间差小时数，时间格式yyyy-MM-dd HH:mm
     * @version currentVersion
     */
    public static double getHourIntervalWithHHMM(String startTime,
                                                 String endTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        Date date1, date2;
        try {
            date1 = formatter.parse(startTime);
            date2 = formatter.parse(endTime);
            cal1.setTime(date1);
            cal2.setTime(date2);
            DecimalFormat df = new DecimalFormat(".#");
            return Double.parseDouble(df.format((cal2.getTimeInMillis() - cal1
                    .getTimeInMillis()) / (1000 * 60 * 60.0)));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }

    /**
     * 计算时间差返回分钟数
     *
     * @param startDate
     * @param endDate
     */
    public static long getDateIntervalWithMinute(String startDate,
                                                 String endDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        Date date1, date2;
        try {
            date1 = formatter.parse(startDate);
            date2 = formatter.parse(endDate);
            cal1.setTime(date1);
            cal2.setTime(date2);
            return (cal2.getTimeInMillis() - cal1.getTimeInMillis())
                    / (1000 * 60);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static long getDateIntervalWithMs(String startDate, String endDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        Date date1, date2;
        try {
            date1 = formatter.parse(startDate);
            if (endDate == null || "".equals(endDate)) {
                System.out.println(endDate);
            }
            date2 = formatter.parse(endDate);
            cal1.setTime(date1);
            cal2.setTime(date2);
            return (cal2.getTimeInMillis() - cal1.getTimeInMillis());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws ParseException {
        String data1 = "2015-09-13";
        SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // return out.format(in.parse(data1));
        // String dateAfter = getDateAfter(data1, 2);
        System.out.println(out.format(in.parse(data1)));
    }

    /**
     *
     * @param startTime
     * @param afterMinute
     * @return
     * @description
     * @version currentVersion
     */
    public static String getMinuteAfterHHmmss(String startTime, int afterMinute) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar cal = Calendar.getInstance();
        Date date;
        try {
            date = formatter.parse(startTime);
            cal.setTime(date);
            cal.add(Calendar.MINUTE, afterMinute);
            String createTime = formatter.format(cal.getTime());
            return createTime;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param startDate
     *            X年Y月
     * @param endDate
     *            X年Y月
     * @version currentVersion
     */
    public static String getAllDatesByStartAndEndDate(String startDate,
                                                      String endDate, String year) {
        startDate = startDate.replace("月", "-").replace("日", "");
        endDate = endDate.replace("月", "-").replace("日", "");
        String allDates = "";
        int i = 1;
        while (true) {
            String middleDate = getDateAfter(year + "-" + startDate, i++);
            if (getDateInterval(middleDate, year + "-" + endDate) > 0) {
                allDates += middleDate.substring(5).replace("-", "月") + "日,";
            } else {
                break;
            }
        }
        return allDates;
    }

    /**
     * 得到本月的第一天
     *
     * @return
     */
    public static String getMonthFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    /**
     * 得到本月的最后一天
     *
     * @return
     */
    public static String getMonthLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,
                calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    /**
     *
     * @return
     * @description 得到当前星期数
     * @version currentVersion
     */
    public static int getCurrentDayWeekDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static int getWeekDay(String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Date date;
        try {
            date = simpleDateFormat.parse(dateStr);
            calendar.setTime(date);
            return calendar.get(Calendar.DAY_OF_WEEK) - 1;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();

        return imei;
    }
}
