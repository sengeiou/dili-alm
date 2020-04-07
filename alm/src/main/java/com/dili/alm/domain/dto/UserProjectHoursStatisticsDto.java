package com.dili.alm.domain.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;

public class UserProjectHoursStatisticsDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5460473647898526683L;

	private Long userId;
	@Excel(name = "姓名", width = 10, needMerge = true)
	private String realName;
	@Excel(name = "总工时", width = 30, needMerge = true, orderNum = "10")
	private Long totalWorkHours;
	@ExcelCollection(name = "项目工时详情")
	private Collection<ProjectHoursStatisticsDto> projectStatistics;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Collection<ProjectHoursStatisticsDto> getProjectStatistics() {
		return projectStatistics;
	}

	public void setProjectStatistics(Collection<ProjectHoursStatisticsDto> projectStatistics) {
		this.projectStatistics = projectStatistics;
	}

	public Long getTotalWorkHours() {
		if (CollectionUtils.isEmpty(this.projectStatistics)) {
			return 0L;
		}
		Long totalWorkHours = 0L;
		for (ProjectHoursStatisticsDto psd : this.projectStatistics) {
			totalWorkHours += psd.getWorkHours();
			totalWorkHours += psd.getOvertimeHours();
		}
		this.totalWorkHours = totalWorkHours;
		return this.totalWorkHours;
	}

}
