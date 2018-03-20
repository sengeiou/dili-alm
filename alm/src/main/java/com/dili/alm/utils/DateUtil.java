package com.dili.alm.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

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
	public static String differentDays(Date date1,Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2)   //同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年            
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }
            
            return timeDistance + (day2-day1) +"";
        }
        else    //不同年
        {
            System.out.println("判断day2 - day1 : " + (day2-day1));
            return day2-day1+"";
        }
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
public static HashMap<String,String>  getFirstAndFive(Date date) {
		
		
		HashMap<String,String> map=new HashMap<String,String>();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//小写的mm表示的是分钟  
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
	public static String getNextMondayAdd(Date gmtCreate) {  
		SimpleDateFormat sdf=new SimpleDateFormat("YYYY-MM-dd");
	    GregorianCalendar currentDate = new GregorianCalendar();  
	    currentDate.add(GregorianCalendar.DATE,   7);  
	    Date monday = currentDate.getTime();  
	   
	    return sdf.format(monday);  
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
	 public static String getNextFiveAdd(Date gmtCreate) {  
		SimpleDateFormat sdf=new SimpleDateFormat("YYYY-MM-dd");
	  
	    GregorianCalendar currentDate = new GregorianCalendar();  
	    currentDate.add(GregorianCalendar.DATE,  13);  
	    Date nextFive = currentDate.getTime();  
	    return  sdf.format(nextFive);  
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

	
    public static Date getStrDate(String date){
		
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date mydate=null;
		try {
			mydate  = sdf.parse(date);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}  
		return   mydate;
	}
 public static Date getStrDateyyyyMMdd(String date){
		
		SimpleDateFormat sdf= new SimpleDateFormat("YYYY-MM-dd");
		Date mydate=null;
		try {
			mydate  = sdf.parse(date);
		} catch (ParseException e) {
			
			e.printStackTrace();
		}  
		return   mydate;
	}
    
   public static String getDateStr(Date date){
		
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mydate=null;
		try {
			mydate  = sdf.format(date);
		} catch (Exception e) {
			
			e.printStackTrace();
		}  
		return   mydate;
	}
	public static String getDate(Date date){

		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		String mydate;
		try {
			mydate  = sdf.format(date);
		} catch (Exception e) {

			e.printStackTrace();
			return "";
		}
		return   mydate;
	}
    
	/***时间比较***/
	public static int compare_date(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {//d1是未来时间，d2是过去时间
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) { //d2是未来时间，d1是过去时间
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static Date appendDateToEnd(Date date){
		try {
			String dateStr = getDate(date);
			dateStr += " 23:59:59";
			return getStrDate(dateStr);
		}catch (Exception e){
			return date;
		}
	}
	public static Date appendDateToStart(Date date){
		try {
			String dateStr = getDate(date);
			dateStr += " 00:00:00";
			return getStrDate(dateStr);
		}catch (Exception e){
			return date;
		}
	}
	 public static String getWeekOfDay(Date dt) {
	        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(dt);
	        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
	        if (w < 0)
	            w = 0;
	        return weekDays[w];
	 }
	 /**
	  * 获取当前时间前第past天的时间
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
	  * @param date1起始时间
	  * @param date2结束时间
	  * @return
	  */
	 public static int differentDaysByMillisecond(Date date1,Date date2)
	    {
	        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
	        return days;
	    }
}
