package com.geely.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class TimeUtil {
	
	/**
	 * 得到某个日期(yyyy-MM-dd)的Date类型
	 * @param str
	 * @return
	 */
	public static Date stringToDate(String str) { 
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
        Date date = null; 
        try { 
            date = format.parse(str);  
        } catch (ParseException e) { 
            e.printStackTrace(); 
        } 
        date = java.sql.Date.valueOf(str);   
        return date; 
    }
	
	/**
	 * 得到某个日期时间点(yyyy-MM-dd HH:mm:ss)的Date类型
	 * @param str
	 * @return
	 */
	public static Date stringToDatetime(String str) { 
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        Date date = null; 
        try { 
            date = format.parse(str);  
        } catch (ParseException e) { 
            e.printStackTrace(); 
        } 
        date = java.sql.Date.valueOf(str);   
        return date; 
    }
	
    /**
     * 得到某个时间点的时间戳
     * @param date
     * @return
     */
    public static long formatDatetime(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            return format.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 得到某个时间戳的时间
     * @param time
     * @return
     */
    public static String formatDatetime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return format.format(time);
    }

    public static String formateDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        return format.format(formatDatetime(date));
    }

    /**
     * 获得当前时间的时间戳
     * @return
     */
    public static long getNowTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 1分钟有多少秒
     * @return
     */
    public static long getMinuteTime() {
        return 1000 * 60;
    }

    /**
     * 1小时有多少秒
     * @return
     */
    public static long getHourTime() {
        return getMinuteTime() * 60;
    }

    /**
     * 1天有多少秒
     * @return
     */
    public static long getDayTime() {
        return getHourTime() * 24;
    }

    /**
     * 获得某个时间点几个小时前或后的时间戳
     * @param time        时间点
     * @param offset        几个小时
     * @return
     */
    public static long getSomeHours(long time, int offset) {
        return getSomeHours(formatDatetime(time), offset);
    }

    /**
     * 获得某个时间点几个小时前或后的时间戳
     * @param date        时间点
     * @param offset        几个小时
     */
    public static long getSomeHours(String date, int offset) {
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d;

        try {
            d = format.parse(date);
            calendar.setTime(d);
            calendar.add(Calendar.HOUR, offset);
            d = calendar.getTime();

            return formatDatetime(format.format(d.getTime()));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 时间点是否在1小时内发生
     * @param date
     * @return
     */
    public static boolean isScopeOfHour(String date) {
        long hourAfter = getSomeHours(date, 1);

        //如果时间1小时后 大于于等于 当前时间
        if (hourAfter >= getNowTimestamp()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 时间点是否在24小时内发生
     * @param date
     * @return
     */
    public static boolean isScopeOfDay(String date) {
        long dayAfter = getSomeHours(date, 24);

        //如果时间24小时后 大于于等于 当前时间
        if (dayAfter >= getNowTimestamp()) {
            return true;
        }

        return false;
    }

    /**
     * 时间点是否在72小时内发生
     * @param date
     * @return
     */
    public static boolean isScopeOfThreeDays(String date) {
        long dayAfter = getSomeHours(date, 72);

        //如果时间72小时后 大于于等于 当前时间
        if (dayAfter >= getNowTimestamp()) {
            return true;
        }

        return false;
    }

    public static String timeRange(String oTime) {
        String answerTime = "";

        switch (TimeUtil.timeStage(oTime)) {
        case 1:
            answerTime = (TimeUtil.getMinutes(oTime) == 0) ? "刚刚"
                                                           : (TimeUtil.getMinutes(oTime) +
                "分钟前");

            break;

        case 2:
            answerTime = TimeUtil.getHours(oTime) + "小时前";

            break;

        case 3:
            answerTime = TimeUtil.getDays(oTime) + "天前";

            break;

        default:
            answerTime = TimeUtil.formateDate(oTime);

            break;
        }

        return answerTime;
    }

    /**
     * 获得一个时间点所在的时间范围，包括1小时内，24小时内，72小时内，超过72小时四种阶段
     * @param date
     * @return
     */
    public static int timeStage(String date) {
        if (isScopeOfHour(date)) {
            return 1; //1小时内
        } else if (isScopeOfDay(date)) {
            return 2; //大于1小时，但在1天内
        } else if (isScopeOfThreeDays(date)) {
            return 3; //大于1天，但在3天内
        } else {
            return 0; //超过72小时
        }
    }

    /**
     * 时间发生在多少分钟前
     * @param date
     * @return
     */
    public static int getMinutes(String date) {
        long difference = getNowTimestamp() - formatDatetime(date);

        //时间还未发生
        if (difference < 0) {
            return 0;
        } else {
            //时间已经发生		(误差值，超过1分钟仍按1分钟计算)
            return (int) (difference / getMinuteTime());
        }
    }

    /**
     * 时间发生在多少小时前
     * @param date
     * @return
     */
    public static int getHours(String date) {
        long difference = getNowTimestamp() - formatDatetime(date);

        //时间还未发生
        if (difference < 0) {
            return 0;
        } else {
            //时间已经发生		(误差值，超过1小时仍按1小时计算)
            return (int) (difference / getHourTime());
        }
    }

    /**
     * 时间发生在多少天前
     * @param date
     * @return
     */
    public static int getDays(String date) {
        long difference = getNowTimestamp() - formatDatetime(date);

        //时间还未发生
        if (difference < 0) {
            return 0;
        } else {
            //时间已经发生		(误差值，超过1天仍按1天计算)
            return (int) (difference / getDayTime());
        }
    }

    /**
     * 判断输入的日期是在今天之前还是今天之后
     * return : -1 今天之前，1 今天之后
     */
    public static int isToday(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date sysTime = new Date();
        Date dbTime;

        try {
            dbTime = dateFormat.parse(time);

            long sysdate = sysTime.getTime();
            long dbdate = dbTime.getTime();
            long diff = sysdate - dbdate;

            if (diff > 0) {
                return -1;
            } else {
                return 1;
            }
        } catch (ParseException e) {
        }

        return 0;
    }

    /**
     * 数据库时间转换成今日分享展示时间
     * @param time
     * @return
     */
    public static String channelTimeFormat(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date sysTime = new Date();
        Date dbTime;
        long sysdate;
        long dbdate;
        long diff;

        int month = 0;
        long day = 0;
        long hour = 0;
        long min = 0;

        try {
            dbTime = dateFormat.parse(time);
            sysdate = sysTime.getTime();
            dbdate = dbTime.getTime();
            diff = sysdate - dbdate;

            day = diff / (24 * 60 * 60 * 1000);
            hour = diff / (60 * 60 * 1000);
            min = diff / (60 * 1000);
            month = (int) (day / 30);

            if (min > 60) {
                if (hour > 24) {
                    if (day > 30) {
                        if (month > 12) {
                            return "1年前";
                        } else {
                            return month + "月前";
                        }
                    } else {
                        return day + "天前";
                    }
                } else {
                    return hour + "小时前";
                }
            } else {
                if (min > 5) {
                    return min + "分钟前";
                } else {
                    return "刚刚";
                }
            }
        } catch (ParseException e) {
        }

        return " ";
    }
}
