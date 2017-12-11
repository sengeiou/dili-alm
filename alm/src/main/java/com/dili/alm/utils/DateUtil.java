package com.dili.alm.utils;

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
	/**
	 * 当前日期添加一天
	 * @param dateStr
	 * @return
	 */
	public static String getAddDay(String dateStr,int n){
		
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		 try {
			date = sdf.parse(dateStr);
		 } catch (ParseException e) {
			e.printStackTrace();
		 }
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);  
		calendar.add(Calendar.DATE, n);//增加一天   
	      
		return    sdf.format(calendar.getTime());  
	}
    public static String getCurenntDay(){
		
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	      
		return    sdf.format(new Date());  
	}

	
	public static String getWeekOfDate(Date dt) { 
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"}; 
        Calendar cal = Calendar.getInstance(); 
        cal.setTime(dt); 

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; 
        if (w < 0) 
            w = 0; 

        return weekDays[w]; 
    } 
	
	public static HashMap<String,String>  getFirstAndFive() {
		
		
		HashMap<String,String> map=new HashMap<String,String>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟  
		java.util.Date date=new Date();
		if(getWeekOfDate(date).endsWith("星期日")){
			String strend=getAddDay(sdf.format(date),-2);//周五
			String  strbegin=getAddDay(sdf.format(date),-6);//周一
			map.put("one", strbegin);
			map.put("five", strend);
		}else{
		    map.put("one", getWeekFristDay());
		    map.put("five", getWeekFriday());
		}
		
		return map;
	}
	public static void main(String[] args) {
		 System.out.println(getNextMonday(new Date()));
		 System.out.println(getNextFive(new Date()));
		

		
    }
	// 获得当前日期与本周日相差的天数  
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
	  
	// 获得下周星期一的日期  
	public static String getNextMonday(Date gmtCreate) {  
		SimpleDateFormat sdf=new SimpleDateFormat("YYYY-MM-dd");
	    int mondayPlus = getMondayPlus(gmtCreate);  
	    GregorianCalendar currentDate = new GregorianCalendar();  
	    currentDate.add(GregorianCalendar.DATE, mondayPlus + 7);  
	    Date monday = currentDate.getTime();  
	    return sdf.format(monday);  
	}  
	// 获得下周星期五的日期  
	public static String getNextFive(Date gmtCreate) {  
		SimpleDateFormat sdf=new SimpleDateFormat("YYYY-MM-dd");
	    int mondayPlus = getMondayPlus(gmtCreate);  
	    GregorianCalendar currentDate = new GregorianCalendar();  
	    currentDate.add(GregorianCalendar.DATE, mondayPlus + 13);  
	    Date nextFive = currentDate.getTime();  
	    return  sdf.format(nextFive);  
	}  

}
