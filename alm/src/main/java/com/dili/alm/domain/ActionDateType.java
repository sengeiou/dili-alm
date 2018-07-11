package com.dili.alm.domain;

public enum ActionDateType {

	PERIOD("时间段", 1), POINT("时间点", 2);

	private String name;
	private Integer value;

	private ActionDateType(String name, Integer value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Integer getValue() {
		return value;
	}

}
