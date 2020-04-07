package com.dili.alm.domain.dto;

import org.apache.commons.lang3.StringUtils;

import com.dili.alm.cache.AlmCache;

import cn.afterturn.easypoi.excel.annotation.Excel;

public class ProjectHoursStatisticsDto {

	private Long projectId;
	@Excel(name = "项目名称", width = 30)
	private String projectName;
	@Excel(name = "项目类型", dict = "projectType", width = 15)
	private String projectType;
	@Excel(name = "常规工时（小时）", width = 20)
	private Long workHours = 0L;
	@Excel(name = "加班工时（小时）", width = 20)
	private Long overtimeHours = 0L;
	@Excel(name = "总工时（小时）", width = 20)
	private Long totalHours = 0L;

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public Long getWorkHours() {
		return workHours;
	}

	public void setWorkHours(Long workHours) {
		this.workHours = workHours;
	}

	public Long getOvertimeHours() {
		return overtimeHours;
	}

	public void setOvertimeHours(Long overtimeHours) {
		this.overtimeHours = overtimeHours;
	}

	public Long getTotalHours() {
		this.totalHours = this.overtimeHours + this.workHours;
		return this.totalHours;
	}

	public String getProjectTypeName() {
		return StringUtils.isNotBlank(this.projectType) ? AlmCache.getInstance().getProjectTypeMap().get(this.projectType) : null;
	}

}