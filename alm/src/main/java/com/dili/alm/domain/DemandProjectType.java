package com.dili.alm.domain;

public enum DemandProjectType {

	PROJECRT("项目", 1), VERSION("版本", 2),WORKORDER("工单", 1);
	private String name;
	private int value;

	DemandProjectType(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

}
