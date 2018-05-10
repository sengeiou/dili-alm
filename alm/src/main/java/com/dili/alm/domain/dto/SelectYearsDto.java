package com.dili.alm.domain.dto;

import java.util.Map;

import com.dili.alm.domain.UserHour;



/****
 * 查询工作日中的年份
 * @author lijing
 *
 */
public class SelectYearsDto  {
	
    String value;
    
    String text;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
    
 
}
