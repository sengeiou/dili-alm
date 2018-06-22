package com.dili.alm.domain.dto;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.dili.alm.domain.ProjectActionRecord;

public class ProjectActionRecordGanttValueDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5064659035889471563L;

	private Long to;
	private Long from;
	private String desc;
	private String label;
	private String customClass;
	private ProjectActionRecord dataObj;

	public Long getTo() {
		return to;
	}

	public Long getFrom() {
		return from;
	}

	public String getDesc() {
		return desc;
	}

	public String getLabel() {
		return label;
	}

	public String getCustomClass() {
		return customClass;
	}

	public ProjectActionRecord getDataObj() {
		return dataObj;
	}

	public void setTo(Long to) {
		this.to = to;
	}

	public void setFrom(Long from) {
		this.from = from;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setCustomClass(String customClass) {
		this.customClass = customClass;
	}

	public void setDataObj(ProjectActionRecord dataObj) {
		this.dataObj = dataObj;
	}

}
