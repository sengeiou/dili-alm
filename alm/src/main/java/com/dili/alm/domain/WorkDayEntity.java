package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkDayEntity extends BaseDomain implements WorkDay {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private Date workStartTime;
	private Date workEndTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getWorkStartTime() {
		return workStartTime;
	}
	public void setWorkStartTime(Date workStartTime) {
		this.workStartTime = workStartTime;
	}
	public Date getWorkEndTime() {
		return workEndTime;
	}
	public void setWorkEndTime(Date workEndTime) {
		this.workEndTime = workEndTime;
	}
	

	
}