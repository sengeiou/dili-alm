package com.dili.alm.domain.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;

@ExcelTarget("TaskStateCountDto")
public class TaskStateCountDto {
	@Excel(name = "任务状态", orderNum = "1", width=20)
	private String taskState;
	@Excel(name="任务数", orderNum = "2")
	private String stateCount;

	public String getTaskState() {
		return taskState;
	}

	public void setTaskState(String taskState) {
		this.taskState = taskState;
	}

	public String getStateCount() {
		return stateCount;
	}

	public void setStateCount(String stateCount) {
		this.stateCount = stateCount;
	}
	
	
}
