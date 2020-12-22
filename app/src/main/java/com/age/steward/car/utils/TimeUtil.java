package com.age.steward.car.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @Description: 时间操作工具包
 * @author 外部引入
 * @created 2015-3-24
 */
public class TimeUtil {

	// 判断当前时间是否在某个时间段之内
	public static boolean judgeTimeRange(int startHour, int startMinute,
			int endHour, int endMinute) {
		if (startHour < 0 || startHour > 24) {
			return false;
		}
		if (startMinute < 0 || startMinute >= 60) {
			return false;
		}
		if (endHour < 0 || endHour > 24) {
			return false;
		}
		if (endMinute < 0 || endMinute >= 60) {
			return false;
		}

		// 时间范围
		final int start = startHour * 60 + startMinute;// 起始时间
		final int end = endHour * 60 + endMinute;// 结束时间
		if (start >= end) {
			return false;
		}

		Calendar cal = Calendar.getInstance();// 当前日期
		int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
		int minute = cal.get(Calendar.MINUTE);// 获取分钟
		int minuteOfDay = hour * 60 + minute;// 当前时间
		// System.out.println("在外围内");
// System.out.println("在外围外");
		return minuteOfDay >= start && minuteOfDay <= end;
	}

	public static String formatQunTime(long qunTime) {
		String timeText = null;
		long currentTime = System.currentTimeMillis();

		int distance = (int) (currentTime - qunTime) / 1000;// 527083
		if (distance < 0)
			distance = 0;
		if (distance < 60) {
			timeText = distance + "秒前";
		} else if (distance < 60 * 60) {
			distance = distance / 60;
			timeText = distance + "分钟前";
		} else if (distance < 60 * 60 * 24) {
			distance = distance / (60 * 60);
			timeText = distance + "小时前";
		} else {
			Date date = new Date(qunTime);
			timeText = DateFormat.getDateInstance(DateFormat.LONG).format(date);
		}
		return timeText;
	}

	/**
	 * 获取当前时间戳，以秒为单位
	 */
	public static String getCurrentTimeMil() {
		String ts = System.currentTimeMillis() / 1000 + "";
		return ts;
	}
	
	/**
	 * 获取当前时间戳，以毫秒为单位
	 */
	public static long getTimeInMillis() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTimeInMillis();
	}
	
	public static String getCurrentTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);
	}
	
	public static String getCurrentData1() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前日期
		return formatter.format(curDate);
	}
	public static String getCurrentData2() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前日期
		return formatter.format(curDate);
	}
	public static String getCurrentYear() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前日期
		return formatter.format(curDate);
	}
	
	/**
	 * 获取当前日期
	 */
	public static String getData() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前日期
		return formatter.format(curDate);
	}
	
	/**
	 * 获取当前日期
	 */
	public static String getData2() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前日期
		return formatter.format(curDate);
	}

	public static long DataTOLong(String data) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		long rand = 0;
		Date d2 = null;
		try {
			d2 = sdf.parse(data);// 将String to Date类型
			rand = d2.getTime();
			return rand;
		} catch (Exception e) {
			Log.e("Exception","Exception:"+e.getMessage());
		}
		return rand;
	}
	
	public static Date StringtoDate(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d2 = null;
		try {
			d2 = sdf.parse(date);// 将String to Date类型
		} catch (Exception e) {
			Log.e("Exception","Exception:"+e.getMessage());
		}
		return d2;
	}
	
	public static Date StringtoDatetime(String datetime){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d2 = null;
		try {
			d2 = sdf.parse(datetime);// 将String to Date类型
		} catch (Exception e) {
			Log.e("Exception","Exception:"+e.getMessage());
		}
		return d2;
	}
	
	public static Date longToDate(long l){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(l * 1000);
		return calendar.getTime();
	}

		
	// long转时间
	public static String getLongToTime(Long l) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(l * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		return format.format(calendar.getTime());
	}

	public static String getLongToTimeTwo(Long l) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(l * 1000);
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		return format.format(calendar.getTime());
	}
	
	public static String getLongToTimeThree(Long l) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(l * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return format.format(calendar.getTime());
	}
	
	public static String getLongToTimeFour(Long l) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(l * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(calendar.getTime());
	}
	
	public static String getLongToDate(long l){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(l * 1000);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(calendar.getTime());
	}

	public static String getStringToString(String l){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = StringtoDate(l);
		return format.format(date);
	}
	
	public static int getYearOfDate(long l){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(l * 1000);
		return calendar.get(Calendar.YEAR);
	}
	
	public static int getMonthOfDate(long l){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(l * 1000);
		return calendar.get(Calendar.MONTH)+1;
	}
	
	public static int getDayOfDate(long l){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(l * 1000);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public static String Millisecond_TO_Time(long l) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(l);// 获取当前时间
		return formatter.format(curDate);
	}

	// 如果年月日相同，则返回传入时间的 HH:mm ， 不同的话返回 MM-dd
	public static String SameDateReturnStr(String inDateTimeStr) {
		if (inDateTimeStr==null||"".equals(inDateTimeStr)||"null".equals(inDateTimeStr)) {
			return "";
		}
		String returnStr = inDateTimeStr;
		try {
			SimpleDateFormat Format_Type0 = new SimpleDateFormat(
					"yyyy-MM-dd hh:mm:ss");
			SimpleDateFormat Format_Type1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat Format_Type2 = new SimpleDateFormat("MM-dd");
			SimpleDateFormat Format_Type3 = new SimpleDateFormat("HH:mm");
			Date nowDate = new Date();// 获取当前日期
			String nowDateStr = Format_Type1.format(nowDate);
			Date inDateTime = Format_Type0.parse(inDateTimeStr);
			String inDataStr = Format_Type1.format(inDateTime);
			if (nowDateStr.equals(inDataStr)) {
				returnStr = Format_Type3.format(inDateTime);
			} else {
				returnStr = Format_Type2.format(inDateTime);
			}
		} catch (Exception e) {
			
		}	
		return returnStr;
	}
	
	public static Date getYearLast(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();  
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();
        return currYearLast;  
    }

	//指定日期加上天数后的日期
	public static String plusDay(int num,String newDate) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date sDate = null;
		try {
			sDate = sdf.parse(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println("Date结束日期:" + sdf.format(sDate));
		Calendar c = Calendar.getInstance();
		c.setTime(sDate);
		c.add(Calendar.DAY_OF_YEAR, num);
		sDate = c.getTime();
		String endDate = sdf.format(sDate);
		return endDate;
	}

	
}
