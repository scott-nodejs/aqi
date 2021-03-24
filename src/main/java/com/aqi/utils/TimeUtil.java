package com.aqi.utils;

import org.springframework.util.Assert;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

    public static String convertMillisToString(Long time) {
        Assert.notNull(time, "time is null");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dtf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }

    public static String convertMillisToMouth(Long time) {
        Assert.notNull(time, "time is null");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM");
        return dtf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()));
    }

    public static Long convertStringToMillis(String time) {
        Assert.notNull(time, "time is null");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parse = LocalDateTime.parse(time, dtf);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long date2TimeStamp(String dateStr,String format) {
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date datetime = null;//将你的日期转换为时间戳
        try {
            datetime = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = datetime.getTime();
         return time/1000;
    }

    public static long getHour(){
        return getHour(System.currentTimeMillis());
    }

    public static long getHour(long longtime){
        Instant instant = Instant.ofEpochMilli(longtime);
        ZoneId zone = ZoneId.systemDefault();
        String time = LocalDateTime.ofInstant(instant, zone).format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
        String hour = time.substring(8);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis()/1000;
    }
}
