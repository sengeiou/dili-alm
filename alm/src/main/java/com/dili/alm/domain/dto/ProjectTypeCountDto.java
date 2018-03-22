package com.dili.alm.domain.dto;

import java.util.List;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
@ExcelTarget("ProjectTypeCountDto")
public class ProjectTypeCountDto {
	@Excel(name="项目类型",orderNum="1",width=20)
	private String type;
	@Excel(name="项目数量",orderNum="2")
	private int typeCount;
	@Excel(name="未开始",orderNum="3")
	private int notStartCount;
	@Excel(name="进行中",orderNum="4")
	private int ongoingConut;
	@Excel(name="已完成",orderNum="5")
	private int completeCount;
	@Excel(name="暂停中",orderNum="6")
	private int suspendedCount;
	@Excel(name="已关闭",orderNum="7")
	private int shutCount;
	
	private int projectTypeProgress;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTypeCount() {
		return typeCount;
	}

	public void setTypeCount(int typeCount) {
		this.typeCount = typeCount;
	}

	public int getNotStartCount() {
		return notStartCount;
	}

	public void setNotStartCount(int notStartCount) {
		this.notStartCount = notStartCount;
	}

	public int getOngoingConut() {
		return ongoingConut;
	}

	public void setOngoingConut(int ongoingConut) {
		this.ongoingConut = ongoingConut;
	}

	public int getCompleteCount() {
		return completeCount;
	}

	public void setCompleteCount(int completeCount) {
		this.completeCount = completeCount;
	}

	public int getSuspendedCount() {
		return suspendedCount;
	}

	public void setSuspendedCount(int suspendedCount) {
		this.suspendedCount = suspendedCount;
	}

	public int getShutCount() {
		return shutCount;
	}

	public void setShutCount(int shutCount) {
		this.shutCount = shutCount;
	}

	public int getProjectTypeProgress() {
		return projectTypeProgress;
	}

	public void setProjectTypeProgress(int projectTypeProgress) {
		this.projectTypeProgress = projectTypeProgress;
	}

	
}
