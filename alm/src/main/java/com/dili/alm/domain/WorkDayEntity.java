package com.dili.alm.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.dto.IBaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;

import java.util.Date;

import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2018-03-22 14:52:13.
 */
public class WorkDayEntity extends BaseDomain implements WorkDay {
   private Long id;
   private Integer wordDayWeek;
   private String workDayYear;
   private Date workStartTime;
   private Date workEndTime;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getWordDayWeek() {
		return wordDayWeek;
	}
	public void setWordDayWeek(Integer wordDayWeek) {
		this.wordDayWeek = wordDayWeek;
	}
	public String getWorkDayYear() {
		return workDayYear;
	}
	public void setWorkDayYear(String workDayYear) {
		this.workDayYear = workDayYear;
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