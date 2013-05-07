package com.geely.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 计算孩子的年龄段
 * @author cherry
 *
 */
public class AgeRangeUtil {
	
	private String inputDate;
	
	private boolean isBirth = false;		//是否已经出生
	private String age = "";				//孩子的年龄段
	private String ageStr = "";				//孩子的年龄段  中文
	private String ageRegion = "";			//孩子属于哪个年龄区间
	private String ageForService = "";
	
	public AgeRangeUtil(){
		
	}
	
	public AgeRangeUtil(String _inputDate){
		inputDate = _inputDate;
	}
	
	public boolean isBirth() {
		return isBirth;
	}
	
	public String getAge() {
		ageStr = age;
		ageStr = ageStr.replaceAll("y", "岁");
		ageStr = ageStr.replaceAll("m", "个月");
		ageStr = ageStr.replaceAll("w", "周");
		ageStr = ageStr.replaceAll("d", "天");
		return ageStr;
	}
	
	public String getAgeChar() {
		return ageForService.toUpperCase();
	}
	
	public String getAgeRegion(){
		return ageRegion;
	}
	
	/**
	 * 以字符串形式返回结果
	 * @return
	 */
	public String getResult(){
		if(isBirth){
			return age+",1";
		}else{
			return age+",0";
		}
	}
	
	/**
	 * 计算孩子当前处于哪个年龄段
	 * @return
	 */
	public void range(){
		long today = getTodayLong();
		long inDay = formateDate(inputDate);
		long difference;
		difference = today - inDay;
		if(difference < 0){
			//生日还到（未出生），数据有误！	当孕期算
			doEDC(inputDate);
		}else{
			doAge(inputDate);
		}
	}
	
	/**
	 * 计算孕期
	 * @param inputDate
	 * @return
	 */
	public void doEDC(String inputDate){
		isBirth = false;
		System.out.println("孕期！");
		ageRegion = "孕期";
		long today = getTodayLong();
		long inDay = formateDate(inputDate);
		long difference = inDay - today;
		String w = getWeek(difference);
//		System.out.println("getWeek = "+w);
		age = w;
		ageForService = w;
	}
	
	/**
	 * 计算年龄
	 * @param inputDate
	 * @return
	 */
	public void doAge(String inputDate){
		isBirth = true;
		long today = getTodayLong();
		long inDay = formateDate(inputDate);
		long difference = today - inDay;
		if(isAgeRange(inputDate,3)){
			System.out.println("已经大于三岁！");
			ageRegion = "学龄前";
			String y = getYear(inputDate);
			age = y;
			ageForService = y;
			if(isAgeRange(inputDate,12)){
				ageRegion = "少年期";
			}else if(isAgeRange(inputDate,6)){
				ageRegion = "童年期";
			}
		}else if(isAgeRange(inputDate,2)){
			System.out.println("已经大于二岁，不到三岁！");
			ageRegion = "幼儿期";
			difference = difference - getYeartime()*2;
			String m = getMonthYearAfter(difference);
			age = "2y"+m;
			ageForService = "2y";
		}else if(isAgeRange(inputDate,1)){
			System.out.println("已经大于一岁，不到二岁！");
			ageRegion = "幼儿期";
			difference = difference - getYeartime();
			String m = getMonthYearAfter(difference);
			age = "1y"+m;
			ageForService = "1y";
		}else{
			System.out.println("已出生，未满一周岁！");
			if(isDayRange(inputDate, 30)){
				System.out.println("已出生，满月！");
				ageRegion = "婴儿期";
				String m = getMonth(difference);
				age = m;
				ageForService = m;
				if(difference == getDaytime() * 30){
					ageRegion = "";
					age = "今天满月！";
				}
			}else{
				System.out.println("已出生，未满月！");
				ageRegion = "新生儿";
				String w = getWeekIsBirth(difference);
				age = w;
				ageForService = w;
				if(difference == getDaytime() * 100){
					ageRegion = "";
					age = "今天100天！";
				}
				
			}
		}
		
		
		
//		if(isAgeRange(inputDate)){
//			System.out.println("已经大于一岁！");
//			String y = getYear(inputDate);
////			System.out.println("getYear = "+y);
//			age = y;
//		}else{
//			long today = getTodayLong();
//			long inDay = formateDate(inputDate);
//			long difference = today - inDay;
//			if(isMonthRange(inputDate)){
//				System.out.println("已出生，未满一周岁！");
//				String m = getMonth(difference);
////				System.out.println("getMonth = "+m);
//				age = m;
//			}else{
//				System.out.println("已出生，未满月！");
//				String w = getWeekIsBirth(difference);
//				age = w;
//			}
//		}
	}
	
	/**
	 * 今天是出生后的第几岁
	 * @param birthday
	 * @return
	 */
	public String getYear(String birthday){
		if(getTodayLong() >= getSomeYear(inputDate,1) && 
				getTodayLong() < getSomeYear(inputDate,2)){
			//如果今天大于等于输入日期1年后，并且小于输入日期2年后。
			return "1y";
		}else if(getTodayLong() >= getSomeYear(inputDate,2) && 
				getTodayLong() < getSomeYear(inputDate,3)){
			//如果今天大于等于输入日期2年后，并且小于输入日期3年后。
			return "2y";
		}else if(getTodayLong() >= getSomeYear(inputDate,3) && 
				getTodayLong() < getSomeYear(inputDate,4)){
			//如果今天大于等于输入日期3年后，并且小于输入日期4年后。
			return "3y";
		}else if(getTodayLong() >= getSomeYear(inputDate,4) && 
				getTodayLong() < getSomeYear(inputDate,5)){
			//如果今天大于等于输入日期4年后，并且小于输入日期5年后。
			return "4y";
		}else if(getTodayLong() >= getSomeYear(inputDate,5) && 
				getTodayLong() < getSomeYear(inputDate,6)){
			//如果今天大于等于输入日期5年后，并且小于输入日期6年后。
			return "5y";
		}else if(getTodayLong() >= getSomeYear(inputDate,6) && 
				getTodayLong() < getSomeYear(inputDate,7)){
			//如果今天大于等于输入日期6年后，并且小于输入日期7年后。
			return "6y";
		}else if(getTodayLong() >= getSomeYear(inputDate,7) && 
				getTodayLong() < getSomeYear(inputDate,8)){
			//如果今天大于等于输入日期7年后，并且小于输入日期8年后。
			return "7y";
		}else if(getTodayLong() >= getSomeYear(inputDate,8) && 
				getTodayLong() < getSomeYear(inputDate,9)){
			//如果今天大于等于输入日期8年后，并且小于输入日期9年后。
			return "8y";
		}else if(getTodayLong() >= getSomeYear(inputDate,9) && 
				getTodayLong() < getSomeYear(inputDate,10)){
			//如果今天大于等于输入日期9年后，并且小于输入日期10年后。
			return "9y";
		}else if(getTodayLong() >= getSomeYear(inputDate,10) && 
				getTodayLong() < getSomeYear(inputDate,11)){
			//如果今天大于等于输入日期10年后，并且小于输入日期11年后。
			return "10y";
		}else if(getTodayLong() >= getSomeYear(inputDate,11) && 
				getTodayLong() < getSomeYear(inputDate,12)){
			//如果今天大于等于输入日期11年后，并且小于输入日期12年后。
			return "11y";
		}else if(getTodayLong() >= getSomeYear(inputDate,12) && 
				getTodayLong() < getSomeYear(inputDate,13)){
			//如果今天大于等于输入日期12年后，并且小于输入日期13年后。
			return "12y";
		}else if(getTodayLong() >= getSomeYear(inputDate,13) && 
				getTodayLong() < getSomeYear(inputDate,14)){
			//如果今天大于等于输入日期13年后，并且小于输入日期14年后。
			return "13y";
		}else if(getTodayLong() >= getSomeYear(inputDate,14) && 
				getTodayLong() < getSomeYear(inputDate,15)){
			//如果今天大于等于输入日期14年后，并且小于输入日期15年后。
			return "14y";
		}else{
			//如果今天大于等于输入日期15年后。
			return "15y";
		}
	}
	
	/**
	 * 今天是出生后的第几个月
	 * @param difference 出生日与今天的相差时间
	 * @return 返回婴儿期
	 */
	public String getMonth(long difference){
		int differenceMonth = 0;
		//得到出生后到今天相差多少天
		int differenceDays = (int) (difference / getDaytime());
		//得到出生后到今天相差多少个月
//		if(0 == differenceDays % 30){
			differenceMonth = differenceDays / 30;
//		}else{
//			differenceMonth = differenceDays / 30 + 1;
//		}
//		System.out.println("differenceDays = "+differenceDays);
//		System.out.println("month = "+differenceMonth);
		//返回当前是出生后的第几个月
		if(12 < differenceMonth){
			//应该按岁计算
			return 12+"m";
		}else if(0 >= differenceMonth){
//			System.out.println("小于或等于0");
			return "1m";
		}else{
//			System.out.println("正常");
			return differenceMonth + "m";
		}
	}
	
	public String getMonthYearAfter(long difference){
		int differenceMonth = 0;
		//得到出生后到今天相差多少天
		int differenceDays = (int) (difference / getDaytime());
		//得到出生后到今天相差多少个月
		if(0 == differenceDays % 30){
			differenceMonth = differenceDays / 30;
		}else{
			differenceMonth = differenceDays / 30 + 1;
		}
//		System.out.println("differenceDays = "+differenceDays);
//		System.out.println("month = "+differenceMonth);
		//返回当前是出生后的第几个月
		if(12 < differenceMonth){
			//应该按岁计算
			return 12+"m";
		}else if(0 >= differenceMonth){
//			System.out.println("小于或等于0");
			return "1m";
		}else{
//			System.out.println("正常");
			return differenceMonth + "m";
		}
	}
	
	/**
	 * 今天是出生后的第几周
	 * @param difference
	 * @return
	 */
	public String getWeekIsBirth(long difference){
		int differenceWeek = 0;
		//得到出生日与今天相差多少天
		int differenceDays = (int) (difference / getDaytime());
		if(0 == differenceDays % 7){
			differenceWeek = differenceDays / 7;
		}else{
			differenceWeek = differenceDays / 7 + 1;
		}
		//返回当前是出生后的第几周
		if(4 < differenceWeek){
			//应该按月计算
			return getMonth(difference);
		}else if(0 >= differenceWeek){
			return 1+"w";
		}else{
			return differenceWeek + "w";
		}
	}
	
	/**
	 * 今天是出生后的第几天
	 * @param difference
	 * @return
	 */
	public String getDayIsBirth(long difference){
		//得到出生日与今天相差多少天
		int differenceDays = (int) (difference / getDaytime());
		if(0 >= differenceDays){
			return 1+"d";
		}else{
			return differenceDays+"d";
		}
	}
	
	/**
	 * 今天是孕期的第几周
	 * @param difference 预产期与今天的相差时间
	 * @return 返回孕期
	 */
	public String getWeek(long difference){
		final int maxCycle = 41;
		int differenceWeek = 0;
		//得到预产期与今天相差多少天
		int differenceDays = (int) (difference / getDaytime());
		//得到预产期与今天相差多少周
		if(0 == differenceDays % 7){
			differenceWeek = differenceDays / 7;
		}else{
			differenceWeek = differenceDays / 7 + 1;
		}
//		System.out.println("differenceDays = "+differenceDays);
//		System.out.println("week = "+differenceWeek);
		//返回当前是孕期的第几周
		if(40 < maxCycle - differenceWeek){
//			System.out.println("大于40");
			return 40+"w";
		}else if(0 >= maxCycle - differenceWeek){
//			System.out.println("小于或等于0");
			return 1+"w";
		}else{
//			System.out.println("正常");
			return (maxCycle - differenceWeek) + "w";
		}
	}
	
	/**
	 * 判断当前时间是否是输入时间几年后
	 * @param inputDate
	 * @param offset
	 * @return
	 */
	public boolean isAgeRange(String inputDate,int offset){
		//生日一年后的时间
		long inYearAfter = getSomeYear(inputDate,offset);
		if(getNow() >= inYearAfter){
			//如果当前时间比输入的时间offset年后  大或等于，表示当前已经是输入时间offset年后
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断当前时间是否是输入时间几个月后
	 * @param inputDate
	 * @param offset
	 * @return
	 */
	public boolean isMonthRange(String inputDate,int offset){
		//生日几个月后的时间
		long inMonthAfter = getSomeMonth(inputDate,offset);
		if(getNow() >= inMonthAfter){
			//如果当前时间比输入的时间几个月后  大或等于，表示当前已经是输入时间的几个月后
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断当前时间是否是输入时间几天后
	 * @param inputDate
	 * @param offset
	 * @return
	 */
	public boolean isDayRange(String inputDate,int offset){
		//生日几天后的时间
		long inDayhAfter = getSomeDay(inputDate,offset);
		if(getNow() >= inDayhAfter){
			//如果当前时间比输入的时间几天后  大或等于，表示当前已经是输入时间的几天后
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 今天的日期
	 * @return
	 */
	public static String getTodayString(){
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		date = calendar.getTime();
		return format.format(date);
	}
	
	/**
	 * 当前时间
	 * @return
	 */
	public static long getNow(){
		return System.currentTimeMillis();
	}
	
	/**
	 * 今天的时间戳
	 * @return
	 */
	public static long getTodayLong(){
		return formateDate(getTodayString());
	}
	
	/**
	 * 某日期某年后的时间戳
	 * @param date 日期
	 * @param offset 相差年
	 * @return
	 */
	public static long getSomeYear(String date,int offset){
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
		Date d;
		try {
			d = format.parse(date);
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(d);
			calendar.add(Calendar.YEAR, offset);
			d = calendar.getTime();
			return formateDate(format.format(d));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 某日期几个月后的时间戳
	 * @param date 日期
	 * @param offset 相差月
	 * @return
	 */
	public static long getSomeMonth(String date,int offset){
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
		Date d;
		try {
			d = format.parse(date);
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(d);
			calendar.add(Calendar.MONTH, offset);
			d = calendar.getTime();
			return formateDate(format.format(d));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 某日期几天后的时间戳
	 * @param date 日期
	 * @param offset 相差天
	 * @return
	 */
	public static long getSomeDay(String date,int offset){
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
		Date d;
		try {
			d = format.parse(date);
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(d);
			calendar.add(Calendar.DATE, offset);
			d = calendar.getTime();
			return formateDate(format.format(d));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 某个时间点的时间戳
	 * @param date
	 * @return
	 */
	public static long formateDate(String date){
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
	    try {
			return format.parse(date).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    return 0;
	}
	
	public static String formatDate(long time){
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
		return format.format(time);
	}
	
	/**
	 * 一天有多少秒
	 * @return
	 */
	public static long getDaytime(){
		return 1000 * 60 * 60 * 24;
	}
	
	/**
	 * 一天月多少秒
	 * @return
	 */
	public static long getMonthtime(){
		return getDaytime() * 30;
	}
	
	/**
	 * 一年有多少秒
	 * @return
	 */
	public static long getYeartime(){
		return getDaytime() * 365;
	}
}
