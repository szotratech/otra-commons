package cn.otra.commons.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public final class DateUtils {

	public final static long DAY_TIMES = 24 * 60 * 60 * 1000;// 一天的毫秒数
	public final static int DAY_HOUR_TIMES = 60 * 60 * 1000;// 一小时的毫秒数
	public final static int DAY_MIN_TIMES = 60 * 1000;// 一分钟的毫秒数
	public final static int DAY_SEC_TIMES = 1000;// 一秒的毫秒数

	public static class TimeFormatter {
		/**
		 * yyyy-MM-dd HH:mm:ss
		 */
		public final static String FORMATTER1 = "yyyy-MM-dd HH:mm:ss";
		/**
		 * yyyy-MM-dd
		 */
		public final static String FORMATTER2 = "yyyy-MM-dd";
		/**
		 * MM-dd HH:mm
		 */
		public final static String FORMATTER3 = "MM-dd HH:mm";
		/**
		 * yyyy-MM-dd HH:mm
		 */
		public final static String FORMATTER4 = "yyyy-MM-dd HH:mm";
		/**
		 * HH:mm
		 */
		public final static String FORMATTER5 = "HH:mm";
		/**
		 * yyyy/MM/dd
		 */
		public final static String FORMATTER6 = "yyyy/MM/dd";
		/**
		 * yyyy-MM
		 */
		public final static String FORMATTER7 = "yyyy-MM";
		/**
		 * yyyy年MM月dd日
		 */
		public final static String FORMATTER8 = "yyyy年MM月dd日";
		/**
		 * HH:mm:ss
		 */
		public final static String FORMATTER9 = "HH:mm:ss";
		/**
		 * MMddHHmmss
		 */
		public final static String FORMATTER10 = "MMddHHmmss";

		public final static String FORMATTER11 = "yyyyMMddHHmmss";

		public final static String FORMATTER12 = "ddMMyyHHmmss.SSS";
		
		public final static String FORMATTER13 = "yyyy-MM-dd HH:mm:ss.S";
		
		public final static String FORMATTER14 = "yyyyMMddHHmmssSSS";

		public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

		public final static String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";

		public final static String YYYY_MM_DD = "yyyy-MM-dd";

		public final static String YYYYMMDD = "yyyyMMdd";
		
		public final static String YYMMDD = "yyMMdd";

	}

	private DateUtils() {
	}

	/**
	 * 将日期转换成指定格式的字符串
	 * 
	 * @param date
	 *            (java.util.Date对象或
	 * @param formatter
	 * @return
	 */
	public static final String dateToString(Date date, String timeFormatter) {
		if (date == null) {
			return null;
		}
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(timeFormatter);
	}

	public static final String dateToString(Long date, String formatter) {
		return dateToString(new Date(date), formatter);
	}

	/**
	 * 获取当前事件字符串
	 */
	public static final String currentDateStr() {
		return dateToString(new Date(), TimeFormatter.FORMATTER1);
	}
	
	public static final String currentDateStr(String timeFormatter) {
		return dateToString(new Date(), timeFormatter);
	}

	/**
	 * 将指定的字符串解析成日期类型
	 * 
	 * @param dateStr
	 *            字符串格式的日期
	 * @return
	 */
	public static Date stringToDate(String dateStr, String pattern) {
		DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
		if(dateStr.length() > pattern.length()) {
			dateStr = dateStr.substring(0,pattern.length());
		}
		return format.parseDateTime(dateStr).toDate();
	}

	public static Date stringToDate(String dateStr){
		if(dateStr.length() == 7){
			return stringToDate(dateStr, TimeFormatter.FORMATTER7);
		}else if(dateStr.length() == 10){
			return stringToDate(dateStr, TimeFormatter.FORMATTER2);
		}else if(dateStr.length() == 19){
			return stringToDate(dateStr, TimeFormatter.FORMATTER1);
		}else if(dateStr.length() == 21){
			return stringToDate(dateStr, TimeFormatter.FORMATTER13);
		}else {
			try {
				return stringToDate(dateStr, TimeFormatter.FORMATTER1);
			} catch (Exception e) {
				throw new RuntimeException("不支持的日期格式。");
			}
			
		}
	}
	
	/**
	 * 检查日期是否是昨天
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isYesterday(long date) {
		DateTime todayStart = getToday(0, 0, 0);
		todayStart.plusDays(-1);
		return date >= todayStart.getMillis() && date <= todayStart.getMillis() + DAY_TIMES;
	}

	/**
	 * 检查日期是否是今天之前的
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isBeforeToday(long date) {
		DateTime todayStart = getToday(0, 0, 0);
		return date < todayStart.getMillis();
	}
	
	/**
	 * 当前时间是否在指定的起止时间内（包含两头）
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isNowBetween(Date start,Date end) {
		long now = System.currentTimeMillis();
		return start.getTime() >= now && now <= end.getTime();
	}
	
	/**
	 * 当前时间是否在指定的起止小时内（包含两头）
	 * @param startHour
	 * @param endHour
	 * @return
	 */
	public static boolean isNowBetweenHour(int startHour,int endHour) {
		return isNowBetween(getTodayDate(startHour, 0, 0), getTodayDate(endHour, 0, 0));
	}

	public static final Date plusHours(Date date, int hours) {
		DateTime dt = new DateTime(date);
		dt = dt.plusHours(hours);
		return dt.toDate();
	}
	
	public static final String plusDays(Date date, int days, String timeFormatter) {
		DateTime dt = new DateTime(date);
		dt = dt.plusDays(days);
		return dt.toString(timeFormatter);
	}
	
	public static final Date plusDays(Date date, int days) {
		DateTime dt = new DateTime(date);
		dt = dt.plusDays(days);
		return dt.toDate();
	}

	public static final String plusHours(Date date, int hours, String timeFormatter) {
		DateTime dt = new DateTime(date);
		dt = dt.plusHours(hours);
		return dt.toString(timeFormatter);
	}

	public static final String plusMins(Date date, int minutes, String timeFormatter) {
		DateTime dt = new DateTime(date);
		dt = dt.plusMinutes(minutes);
		return dt.toString(timeFormatter);
	}
	
	public static final Date plusMins(Date date, int minutes) {
		DateTime dt = new DateTime(date);
		dt = dt.plusMinutes(minutes);
		return dt.toDate();
	}
	
	public static final Date plusSeconds(Date date, int seconds) {
		DateTime dt = new DateTime(date);
		dt = dt.plusSeconds(seconds);
		return dt.toDate();
	}

	public static final String plusMonths(Date date, int months, String timeFormatter) {
		DateTime dt = new DateTime(date);
		dt = dt.plusMonths(months);
		return dt.toString(timeFormatter);
	}
	
	public static final Date plusMonths(Date date, int months) {
		DateTime dt = new DateTime(date);
		dt = dt.plusMonths(months);
		return dt.toDate();
	}

	public static final Date plusYears(Date date, int years) {
		DateTime dt = new DateTime(date);
		dt = dt.plusYears(years);
		return dt.toDate();
	}
	
	/**
	 * 检查日期是否在今天之后
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isAfterToday(long date) {
		DateTime todayStart = getToday(23, 59, 59);
		return date > todayStart.getMillis();
	}

	public static DateTime getToday(int hour, int min, int sec) {
		DateTime now = new DateTime();
		DateTime todayStart = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hour, min, sec);
		return todayStart;
	}
	
	/**
	 * 获取指定天的开始
	 * @param day
	 * @param hour
	 * @param min
	 * @param sec
	 * @return
	 */
	public static Date getDay(Date day,int hour, int min, int sec) {
		DateTime dateTime = new DateTime(day);
		DateTime todayStart = new DateTime(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), hour, min, sec);
		return todayStart.toDate();
	}
	
	public static Date getTodayDate(int hour, int min, int sec) {
		DateTime now = new DateTime();
		DateTime todayStart = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hour, min, sec);
		return todayStart.toDate();
	}

	public static DateTime getDateTime(int year, int month, int day, int hour, int min, int sec) {
		return new DateTime(year, month, day, hour, min, sec);
	}

	public static Date getDate(int year, int month, int day, int hour, int min, int sec) {
		return getDateTime(year, month, day, hour, min, sec).toDate();
	}

	/**
	 * 是否是今天
	 * 
	 * @return
	 */
	public static boolean isToday(long date) {
		DateTime todayStart = getToday(0, 0, 0);
		return date >= todayStart.getMillis() && date <= todayStart.getMillis() + DAY_TIMES;
	}

	public static int getWeekDay() {
		Calendar today = Calendar.getInstance();
		return today.get(Calendar.DAY_OF_WEEK);
	}

	public static boolean isWorkDay() {
		int weekDay = DateUtils.getWeekDay();
		if (weekDay == Calendar.SATURDAY || weekDay == Calendar.SUNDAY) {
			return false;
		}
		return true;
	}

	/**
	 * 得到今天0时时间
	 * 
	 * @return
	 */
	public static long getToday() {
		return getToday(0, 0, 0).getMillis();
	}

	/**
	 * 得到今天指定时分秒的毫秒数
	 * 
	 * @param str
	 *            HH:mm:ss
	 * @return
	 */
	public static long getTodayTime(int hour, int minute, int second) {
		return getToday(hour, minute, second).getMillis();
	}

	/**
	 * 返回当天的月数(1月返回1,依此类推)
	 * 
	 * @return
	 */
	public static int getMonth() {
		Calendar today = Calendar.getInstance();
		return today.get(Calendar.MONTH) + 1;
	}

	public static int getPreMonth() {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.MONTH, -1);
		return today.get(Calendar.MONTH) + 1;
	}

	/**
	 * 返回当天的号数
	 * 
	 * @return
	 */
	public static int getDay() {
		return new DateTime().getDayOfMonth();
	}

	/**
	 * 返回指定时间的号数
	 * 
	 * @return
	 */
	public static int getDay(Date date) {
		return new DateTime(date).getDayOfMonth();
	}
	
	/**
	 * 返回指定时间的月数
	 * 
	 * @return
	 */
	public static int getMonth(Date date) {
		return new DateTime(date).getMonthOfYear();
	}

	/**
	 * 取得当前月的最大天数
	 * 
	 * @return
	 */
	public static int getMaxDayInMon() {
		Calendar c = Calendar.getInstance();
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static int getMaxDayInMon(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static int getYear() {
		return new DateTime().getYear();
	}

	public static int getPerMonth() {
		DateTime dateTime = new DateTime();
		dateTime = dateTime.plusMonths(-1);
		return dateTime.getMonthOfYear();
	}

	/**
	 * 获取两时间差的描述(xx天xx时xx分)
	 * @param start
	 * @param end
	 * @return
	 */
	public static String getDaysBetweenWithDayAndMin(Date start, Date end) {
		long between = Seconds.secondsBetween(new DateTime(start), new DateTime(end)).getSeconds();
		long day = between / (24 * 3600);
		long hour = between % (24 * 3600) / 3600;
		long min = between % 3600 / 60;
		if(day < 0 || hour < 0 || min < 0) {
			day = 0;
			hour = 0;
			min = 0;
		}
		return day+" 天 "+hour+" 小时 "+min+" 分钟";
	}
	
	/**
	 * 获取两个时间的分秒描述（xx分xx秒）
	 * @param start
	 * @param end
	 * @return
	 */
	public static String getMinLeftTimeString(Date start, Date end) {
		long between = Seconds.secondsBetween(new DateTime(start), new DateTime(end)).getSeconds();
		long min = between / 60;
		long sec = between % 60;
		if(min < 0) {
			min = 0;
		}
		if(sec < 0) {
			sec = 0;
		}
		return min+"分 "+sec+"秒";
	}
	
	public static String getMinLeftTimeString(Date date) {
		return getMinLeftTimeString(new Date(), date);
	}
	
	public static final long getLeftSeconds(Date date) {
		return Seconds.secondsBetween(new DateTime(), new DateTime(date)).getSeconds();
	}
	
	public static String getMinLeftTimeString(long seconds) {
		if(seconds < 0) {
			return "0 分 0 秒";
		}
		long min = seconds / 60;
		long sec = seconds % 60;
		if(min < 0) {
			min = 0;
		}
		if(sec < 0) {
			sec = 0;
		}
		return min+"分 "+sec+"秒";
	}
	
	
	public static int getSecondsBetween(Date start, Date end) {
		return Seconds.secondsBetween(new DateTime(start), new DateTime(end)).getSeconds();
	}
	
	public static int getDaysBetween(Date start, Date end) {
		return Days.daysBetween(new DateTime(start), new DateTime(end)).getDays();
	}
	
	public static int getYearsBetween(Date start, Date end) {
		return Years.yearsBetween(new DateTime(start), new DateTime(end)).getYears();
	}
	
	public static int getDaysBetween(String start, String end) {
		return getDaysBetween(DateUtils.stringToDate(start), DateUtils.stringToDate(end));
	}
	
	public static int getDaysBetweenInTimeLevelCase(Date start, Date end) {		
		return getDaysBetween(start, end);
	}

	public static int getDaysBetweenInDateLevelCase(Date start, Date end) {		
		return getDaysBetween(DateUtils.dateToString(start, TimeFormatter.FORMATTER2), DateUtils.dateToString(end, TimeFormatter.FORMATTER2));
	}
	
	/**
	 * 获取指定开始时间到结束时间总共包含的月数,时间区间为:[startDate,endDate)
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 */
	public static long containMonths(Date startDate, Date endDate) throws ParseException {
		return Months.monthsBetween(new DateTime(startDate), new DateTime(endDate)).getMonths();
	}

	public static long containDays(Date startDate, Date endDate) throws ParseException {
		return Days.daysBetween(new DateTime(startDate), new DateTime(endDate)).getDays();
		// return Months.monthsBetween(new DateTime(startDate), new
		// DateTime(endDate)).getMonths();
	}

	/**
	 * 返回给定时间加上plusValue天后的字符串形式
	 * 
	 * @param date
	 *            指定时间
	 * @param plusValue
	 *            天数，或以为负数
	 * @param formatter
	 *            返回的时间格式
	 * @return
	 */
	public static String dateToString(Date date, int plusValue, String formatter) {
		DateTime dateTime = new DateTime(date);
		dateTime = dateTime.plusDays(plusValue);
		return dateTime.toString(formatter);
	}

	/**
	 * 将给定的毫秒时间段转换成"时:分:秒"格式
	 * 
	 * @param interval
	 *            毫秒
	 * @return
	 */
	public static String getFomaterTime(long interval) {
		StringBuffer time = new StringBuffer();
		long h = interval / DAY_HOUR_TIMES;
		interval = interval - h * DAY_HOUR_TIMES;
		long m = interval / DAY_MIN_TIMES;
		interval = interval - m * DAY_MIN_TIMES;
		long s = interval / DAY_SEC_TIMES;
		return time.append(h).append(":").append(m).append(":").append(s).toString();
	}

	/**
	 * 获得本周周一的日期(时间为当前时间)
	 * 
	 * @return
	 */
	public static Date getMonday() {
		Calendar cal = Calendar.getInstance();
		int day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 2;
		cal.add(Calendar.DATE, -day_of_week);
		return cal.getTime();
	}

	/**
	 * @title:获取当前日期的前几天或者后几天日期
	 * @param: day 天数 负数代表前几天，正数代表后几天
	 * @return
	 */
	public static Date getDateByDay(int day) {
		// 获取当前日期
		Calendar date = Calendar.getInstance();
		date.set(Calendar.DATE, date.get(Calendar.DATE) + day);
		return date.getTime();
	}
	
	/**
	 * @title:获取当前日期的前几年或者后几年
	 * @param: year 天数 负数代表前几年，正数代表后几年
	 * @return
	 */
	public static Date getDateByYear(int year) {
		// 获取当前日期
		Calendar date = Calendar.getInstance();
		date.set(Calendar.YEAR, date.get(Calendar.YEAR) + year);
		return date.getTime();
	}
	
	public static final String getGMT(Date date) {
		SimpleDateFormat gmtSimpleDateFormat = new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		gmtSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));  
		String dt = gmtSimpleDateFormat.format(date);
		return dt;
	}

	/**
	 * 获取本周星期几的当前日期
	 * @param value 星期几，从1开始。1表示星期一，7表示星期天
	 * @return
	 */
	public static final Date getWeekDay(int value) {
		if(value<1 || value > 7) {
			throw new RuntimeException("value must in [1,7],but "+value);
		}
		return plusDays(new Date(), value +1+getWeekDay()*-1);
	}
	
	public static final Date getWeekBegin() {
		return getWeekDay(1, 0, 0, 0);
	}
	
	/**
	 * 获取本周星期几的开始日期
	 * @param value
	 * @return
	 */
	public static final Date getWeekDayBegin(int value) {
		return getWeekDay(value, 0, 0, 0);
	}
	
	public static final Date getWeekEnd() {
		return getWeekDay(7, 23, 59, 59);
	}
	
	public static final Date getYearBegin() {
		int year = getYear();
		return stringToDate(year+"-01-01 00:00:00",TimeFormatter.FORMATTER1);
	}
	
	public static final Date getYearEnd() {
		int year = getYear();
		return stringToDate(year+"-12-31 23:59:59",TimeFormatter.FORMATTER1);
	}
	
	/**
	 * 获取本周星期几的指定时分秒日期
	 * @param value
	 * @param hour
	 * @param min
	 * @param sec
	 * @return
	 */
	public static final Date getWeekDay(int value,int hour,int min,int sec) {
		if(value<1 || value > 7) {
			throw new RuntimeException("value must in [1,7],but "+value);
		}
		return getDay(plusDays(new Date(), value +1+getWeekDay()*-1), hour, min, sec);
	}
	/**
	 * 获取本月开始日期
	 * @return
	 */
	public static final Date getMonthDayBegin() {
		DateTime dateTime = new DateTime();
		Date date = dateTime.dayOfMonth().withMinimumValue().toDate();
		return getDay(date, 0, 0, 0);
	}
	
	public static final Date getMonthDayEnd() {
		DateTime dateTime = new DateTime();
		Date date = dateTime.dayOfMonth().withMaximumValue().toDate(); 
		return getDay(date, 23, 59, 59);
	}
	
	public static final Date getMonthDay(int value,int hour,int min,int sec) {
		DateTime dateTime = new DateTime();
		int day = dateTime.getDayOfMonth();
		Date date = plusDays(new Date(), value-day);
		return getDay(date, 0, 0, 0);
	}
	
	public static void main(String args[]) throws ParseException {
		System.err.println(stringToDate("2015-06-20 12:42:57.tfger0", TimeFormatter.FORMATTER1));
	}
}
