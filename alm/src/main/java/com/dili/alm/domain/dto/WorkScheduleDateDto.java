package com.dili.alm.domain.dto;

import com.dili.ss.domain.BaseDomain;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkScheduleDateDto extends BaseDomain  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String scheduleDate;

	private int textCount;


	public String getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate.substring(0, 10);
	}

	public int getTextCount() {
		return textCount;
	}

	public void setTextCount(int textCount) {
		this.textCount = textCount;
	}


	

}