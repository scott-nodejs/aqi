package com.aqi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @ClassName: DateUtil
 * @Description: 时间转化工具类
 * @author han
 * @date 2016年1月4日 上午10:30:32
 */
public class DateUtil {
	public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 当前日期加n天
	 * 
	 * @param addDay
	 * @return
	 * @author leamo
	 * @date 2016年12月20日 下午1:33:26
	 */
	public static Date addDay(int addDay) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, addDay);
		return calendar.getTime();
	}

	public static Date addHour(int addDay) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, addDay);
		return calendar.getTime();
	}

	/**
	 * 将字符串转成yyyy-MM-dd HH:mm:ss的时间
	 * 
	 * @param date
	 * @return
	 * @author lin
	 * @date 2016年7月22日下午3:15:26
	 */
	public static Date stringToDate(String date) {
        return date == null ? null : stringToDate(date, YMDHMS);
	}

	/**
	 * 字符串转date
	 * 
	 * @param date：需要转的字符串
	 * @param format：格式化标准
	 * @return
	 * @author lin
	 * @date 2016年7月22日下午3:14:17
	 */
	public static Date stringToDate(String date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date time = null;
		try {
			time = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return time;
	}
    /**
     * 将当前时间格式化为“yyyyMMdd”格式的字符串
     * 
     * @return
     */
    public static String getDateToString() {
        Date date = new Date();
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyMMdd");
        return simpleFormat.format(date);
    }

    /**
     * 自定义格式化当前时间
     * 
     * @param format
     * @return
     * @author lin
     * @date 2016年4月8日下午3:18:34
     */
    public static String getDateToString(String format) {
        Date date = new Date();
        SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
        return simpleFormat.format(date);
    }

	/**
	 * 自定义格式化输入时间
	 * 
	 * @param date
	 * @param format
	 * @return
	 * @author lin
	 * @date 2016年6月22日上午10:15:00
	 */
	public static String getDateToString(Date date, String format) {
		SimpleDateFormat simpleFormat = new SimpleDateFormat(format);
		return simpleFormat.format(date);
	}

    public static String formatPastTime(Date date) {
        long pastTime = new Date().getTime() - date.getTime();
        if (pastTime < 1000 * 60) {
            return "刚刚";
        }
        if (pastTime < 1000 * 60 * 60) {
            return pastTime / (1000 * 60) + "分钟前";
        }
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (dayFormat.format(date).equals(dayFormat.format(new Date()))) {
            if (pastTime < 1000 * 60 * 60 * 12) {
                return pastTime / (1000 * 60 * 60) + "小时前";
            }
            if (pastTime < 1000 * 60 * 60 * 24) {
                return "今天";
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        if (dayFormat.format(date).equals(dayFormat.format(calendar.getTime()))) {
            return "昨天";
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_YEAR, -2);
        if (dayFormat.format(date).equals(dayFormat.format(calendar1.getTime()))) {
            return "2天前";
        }
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_YEAR, -3);
        if (dayFormat.format(date).equals(dayFormat.format(calendar2.getTime()))) {
            return "3天前";
        }

        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static void main(String[] args) throws ParseException {
//        System.out.println(stringToDate(null));
//        System.out.println(getDateToString("yyyyMMdd"));
//        System.out.println(getDateToString("yyMMdd"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse("2020-02-06 10:00:00");
        System.out.println(formatPastTime(date));
    }
}