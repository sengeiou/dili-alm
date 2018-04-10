package com.dili.alm.domain;

public enum HardwareApplyOperationType {

	PROJECT_MANAGER(1, "项目经理"), GENERAL_MANAGER(2, "研发部经理"), OPERATION_MANAGER(3, "运维部经理"), EXECUTOR(4, "实施人");

	private Integer value;
	private String name;

	private HardwareApplyOperationType(Integer value, String name) {
		this.value = value;
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

}
