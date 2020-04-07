package com.dili.alm.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {
	public static String getWeekFristDay() {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
		return df.format(cal.getTime());
	}

	public static String getToDay() {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		return df.format(cal.getTime());
	}

	public static String getWeekFriday() {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = new GregorianCalendar();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + 4);
		Date last = cal.getTime();
		return formater.format(last);
	}

	public static String getDatePoor(Date endDate, Date nowDate) {

		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		// long ns = 1000;
		// 获得两个时间的毫秒时间差异
		long diff = endDate.getTime() - nowDate.getTime();
		// 计算差多少天
		long day = diff / nd;
		// 计算差多少小时
		// long hour = diff % nd / nh;
		// 计算差多少分钟
		// long min = diff % nd % nh / nm;
		// 计算差多少秒//输出结果
		// long sec = diff % nd % nh % nm / ns;
		return day + "";
	}

	public static String differentDays(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		int day1 = cal1.get(Calendar.DAY_OF_YEAR);
		int day2 = cal2.get(Calendar.DAY_OF_YEAR);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		if (year1 != year2) // 同一年
		{
			int timeDistance = 0;
			for (int i = year1; i < year2; i++) {
				if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) // 闰年
				{
					timeDistance += 366;
				} else // 不是闰年
				{
					timeDistance += 365;
				}
			}

			return timeDistance + (day2 - day1) + "";
		} else // 不同年
		{
			System.out.println("判断day2 - day1 : " + (day2 - day1));
			return day2 - day1 + "";
		}
	}

	public static String getCurenntDay() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return sdf.format(new Date());
	}

	// 获得当前日期与本周一相差的天数
	private static int getMondayPlus(Date gmtCreate) {
		Calendar cd = Calendar.getInstance();
		cd.setTime(gmtCreate);
		// 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
		if (dayOfWeek == 1) {
			return 0;
		} else {
			return 1 - dayOfWeek;
		}
	}

	public static Date getStrDate(String date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date mydate = null;
		try {
			mydate = sdf.parse(date);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return mydate;
	}

	public static Date getStrDateyyyyMMdd(String date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date mydate = null;
		try {
			mydate = sdf.parse(date);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return mydate;
	}

	public static String getDateStr(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mydate = null;
		try {
			mydate = sdf.format(date);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return mydate;
	}

	public static String getDate(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String mydate;
		try {
			mydate = sdf.format(date);
		} catch (Exception e) {

			e.printStackTrace();
			return "";
		}
		return mydate;
	}

	/*** 时间比较 ***/
	public static int compare_date(String DATE1, String DATE2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {// d1是未来时间，d2是过去时间
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) { // d2是未来时间，d1是过去时间
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	public static Date appendDateToEnd(Date date) {
		try {
			String dateStr = getDate(date);
			dateStr += " 23:59:59";
			return getStrDate(dateStr);
		} catch (Exception e) {
			return date;
		}
	}

	public static Date appendDateToStart(Date date) {
		try {
			String dateStr = getDate(date);
			dateStr += " 00:00:00";
			return getStrDate(dateStr);
		} catch (Exception e) {
			return date;
		}
	}

	public static String getWeekOfDay(Date dt) {
		String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0) {
			w = 0;
		}
		return weekDays[w];
	}

	/**
	 * 获取当前时间前第past天的时间
	 * 
	 * @param past
	 * @return
	 */
	public static String getPastDate(int past) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
		Date today = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String result = format.format(today);
		return result;
	}

	/**
	 * 获取当前时间后第past天的时间
	 * 
	 * @param past
	 * @return
	 */
	public static String getFutureDate(int past) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
		Date today = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String result = format.format(today);
		return result;
	}

	/**
	 * 相差多少天
	 * 
	 * @param date1起始时间
	 * @param date2结束时间
	 * @return
	 * @throws ParseException
	 */
	public static int differentDaysByMillisecond(Date date1, Date date2) {
		Date time1 = DateUtil.getStrDateyyyyMMdd(DateUtil.getDate(date1));
		Date time2 = DateUtil.getStrDateyyyyMMdd(DateUtil.getDate(date2));
		int days = (int) ((time2.getTime() - time1.getTime()) / (1000 * 3600 * 24));
		return days;
	}

	/**
	 * <pre>
	 * 
	 * 获取某月份第N周开始日期(即星期一)
	 * </pre>
	 * 
	 * @param year      年
	 * @param month     月
	 * @param weekOrder 周次
	 * @return Date 某月份第N周开始日期,即周一(包含跨月),比如2014年1月的第一周的第一天为2013-12-30,也就是说,2014
	 *         年1月的第一周也就是2013年12月的最后一周
	 */
	public static Date getFirstDayOfWeekOrder(int year, int month, int weekOrder) {

		final Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, 1); // 设为每个月的第一天(1号)

		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK); // 每个月的第一天为星期几

		/*
		 * 星期日:1,星期一:2,星期二:3,星期三:4,星期四:5,星期五:6,星期六:7
		 * 转化为我们的使用习惯:星期一:1,星期二:2,星期三:3,星期四:4,星期五:5,星期六:6,星期日:7
		 */
		if (dayOfWeek != Calendar.SUNDAY) {
			dayOfWeek = dayOfWeek - 1;
		} else {
			dayOfWeek = 7;
		}
		c.add(Calendar.DAY_OF_MONTH, 1 - dayOfWeek); // 使其为每个月第一天所在周的星期一
		c.add(Calendar.DAY_OF_MONTH, (weekOrder - 1) * 7);

		return c.getTime();
	}

	/**
	 * <pre>
	 * 
	 * 获取某月份第N周结束日期(即星期日)
	 * </pre>
	 * 
	 * @param year      年
	 * @param month     月
	 * @param weekOrder 周次
	 * @return Date 某月份第N周结束日期,即周日(包含跨月),比如2013年11月的第五周的最后一天为2013-12-01,也就是说,2013
	 *         年11月第五周也就是2013年12月的第一周
	 */
	public static Date getLastDayOfWeekOrder(int year, int month, int weekOrder) {
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, 1); // 设为每个月的第一天(1号)

		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK); // 每个月的第一天为星期几

		/*
		 * 星期日:1,星期一:2,星期二:3,星期三:4,星期四:5,星期五:6,星期六:7
		 * 转化为我们的使用习惯:星期一:1,星期二:2,星期三:3,星期四:4,星期五:5,星期六:6,星期日:7
		 */
		if (dayOfWeek != Calendar.SUNDAY) {
			dayOfWeek = dayOfWeek - 1;
		} else {
			dayOfWeek = 7;
		}
		c.add(Calendar.DAY_OF_MONTH, 1 - dayOfWeek); // 使其为每个月第一天所在周的星期一
		c.add(Calendar.DAY_OF_MONTH, (weekOrder - 1) * 7 + 6);

		return c.getTime();
	}

	/**
	 * 每个月最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date lastDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);// 设定当前时间为每月一号
		// 当前日历的天数上-1变成最大值 , 此方法不会改变指定字段之外的字段
		calendar.roll(Calendar.DAY_OF_MONTH, -1);
		return calendar.getTime();
	}

	public static Map<String, String> getWeeks(int year, int month) {
		Map<String, String> weekDateMap = new HashMap<String, String>();
		for (int i = 0; i < 6; i++) {// 6*7=42 一个月最大覆盖6周，即每月1号是周日，每月最后一天是周一
			Date da2 = DateUtil.getFirstDayOfWeekOrder(year, month, i + 1);

			Date lastDa2 = DateUtil.getLastDayOfWeekOrder(year, month, i + 1);
			// 获取默认选中的日期的年月日星期的值，并赋值
			Calendar start = Calendar.getInstance();// 日历对象
			start.setTime(da2);// 设置当前日期

			Calendar end = Calendar.getInstance();// 日历对象
			end.setTime(lastDa2);// 设置当前日期
			if (start.get(Calendar.MONTH) + 1 != month && end.get(Calendar.MONTH) + 1 == month) {// 本周开始日期不本月的
				String beiginDate = year + "-0" + month + "-01 00:00:00";
				da2 = DateUtil.getStrDate(beiginDate);

			}

			if (start.get(Calendar.MONTH) + 1 == month && end.get(Calendar.MONTH) + 1 != month) {
				String beiginDate = year + "-0" + month + "-01 00:00:00";
				Date past = DateUtil.getStrDate(beiginDate);
				lastDa2 = DateUtil.lastDayOfMonth(past);
			}
			/*
			 * System.out.println("-----------------第"+(i+1)+"周-------------------------");
			 * System.out.println("开始日期:"+new
			 * SimpleDateFormat("yyyy-MM-dd").format(da2)+";");
			 * System.out.println("结束日期:"+new
			 * SimpleDateFormat("yyyy-MM-dd").format(lastDa2)+"。");
			 */

			weekDateMap.put("start" + i, new SimpleDateFormat("yyyy-MM-dd").format(da2));
			weekDateMap.put("end" + i, new SimpleDateFormat("yyyy-MM-dd").format(lastDa2));
			if (start.get(Calendar.MONTH) + 1 == month && end.get(Calendar.MONTH) + 1 > month) {
				break;
			}
		}

		return weekDateMap;
	}

	public static int getCompareDate(Date s1, Date s2) {
		java.util.Calendar c1 = java.util.Calendar.getInstance();
		java.util.Calendar c2 = java.util.Calendar.getInstance();
		c1.setTime(s1);
		c2.setTime(s2);
		return c1.compareTo(c2);
		/*
		 * if(result==0) System.out.println("c1相等c2"); else if(result<0)
		 * System.out.println("c1小于c2"); else System.out.println("c1大于c2");
		 */
	}

	public static String getStrYear(Date dateCellValue) {
		Calendar c = Calendar.getInstance();
		c.setTime(dateCellValue);
		int i = c.get(Calendar.YEAR);
		return String.valueOf(i);
	}

	/**
	 * 获取当年的第一天
	 * 
	 * @param year
	 * @return
	 */
	public static Date getCurrYearFirst() {
		Calendar currCal = Calendar.getInstance();
		int currentYear = currCal.get(Calendar.YEAR);
		return getYearFirst(currentYear);
	}

	/**
	 * 获取当年的最后一天
	 * 
	 * @param year
	 * @return
	 */
	public static Date getCurrYearLast() {
		Calendar currCal = Calendar.getInstance();
		int currentYear = currCal.get(Calendar.YEAR);
		return getYearLast(currentYear);
	}

	/**
	 * 获取某年第一天日期
	 * 
	 * @param year 年份
	 * @return Date
	 */
	public static Date getYearFirst(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		Date currYearFirst = calendar.getTime();
		return currYearFirst;
	}

	/**
	 * 获取某年最后一天日期
	 * 
	 * @param year 年份
	 * @return Date
	 */
	public static Date getYearLast(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		Date currYearLast = calendar.getTime();

		return currYearLast;
	}

}
