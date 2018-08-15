package com.dili.alm.domain;

public enum TravelCostApplyType {

	PROJECT("项目", 1), CUSTOM("自定义", 2);

	private String name;
	private Integer value;

	private TravelCostApplyType(String name, Integer value) {
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
