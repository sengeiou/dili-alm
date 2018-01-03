package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkScheduleEntity extends BaseDomain implements WorkSchedule {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Date scheduleDate;

	private String scheduleText;

	private Long userId;

	private String scheduleDateStr;// 将date日期转化为String 类型

	public WorkScheduleEntity(WorkSchedule entity) {
		this.id = entity.getId();
		this.scheduleDate = entity.getScheduleDate();
		this.scheduleText = entity.getScheduleText();
		this.userId = entity.getUserId();
		this.scheduleDateStr = new SimpleDateFormat("yyyy-MM-dd").format(entity.getScheduleDate());
	}

	public String getScheduleDateStr() {
		return scheduleDateStr;
	}

	public void setScheduleDateStr(String scheduleDateStr) {
		this.scheduleDateStr = scheduleDateStr;
	}

	@Override
	public Date getScheduleDate() {
		return scheduleDate;
	}

	@Override
	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;

	}

	@Override
	public String getScheduleText() {
		return scheduleText;
	}

	@Override
	public void setScheduleText(String scheduleText) {
		this.scheduleText = scheduleText;
	}

	@Override
	public Long getUserId() {
		return userId;
	}

	@Override
	public void setUserId(Long userId) {
		userId = this.userId;
	}

}