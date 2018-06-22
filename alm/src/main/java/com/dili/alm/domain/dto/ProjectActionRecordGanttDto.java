package com.dili.alm.domain.dto;

import java.io.Serializable;
import java.util.List;

public class ProjectActionRecordGanttDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3215114482988269760L;

	private String name;
	private String desc;
	private List<ProjectActionRecordGanttValueDto> values;

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public List<ProjectActionRecordGanttValueDto> getValues() {
		return values;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setValues(List<ProjectActionRecordGanttValueDto> values) {
		this.values = values;
	}

}
