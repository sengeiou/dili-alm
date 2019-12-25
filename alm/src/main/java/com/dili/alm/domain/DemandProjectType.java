package com.dili.alm.domain;

public enum DemandProjectType {

	APPLY("立项", 1), VERSION("版本", 2),WORKORDER("工单", 3);
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
