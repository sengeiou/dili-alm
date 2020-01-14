package com.dili.alm.utils;
import javax.persistence.Column;

import com.dili.alm.domain.Demand;
import com.dili.alm.domain.dto.DemandDto;

import java.lang.reflect.Field;
public class ColumnUtil {
	
		public static void main(String[] args) {
		    String str=getColumnValue(Demand.class,"serialNumber");
		    System.out.println(str);
		}

		public static String getColumnValue(Class classz,String property) {
	        Field[] fields=classz.getDeclaredFields();
	        Field field;
	        for (int i = 0; i <fields.length ; i++) {
	            fields[i].setAccessible(true);
	        }
	        for(int i = 1;i<fields.length ; i++){
	            try {
	                if(property.equals(fields[i].getName())) {
	                	field=classz.getDeclaredField(fields[i].getName());
	                	Column column=field.getAnnotation(Column.class); //获取指定类型注解
	                	return column.name().replace("`", "");
	                }
	            } catch (NoSuchFieldException e) {
	                e.printStackTrace();
	            }
	        }
	        return null;
		}

}
