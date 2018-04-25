package com.dili.alm.domain;

public enum TravelCostApplyResult {

	PASS("通过", 1), REJECT("拒绝", 0);

	private String name;
	private Integer value;

	private TravelCostApplyResult(String name, Integer value) {
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
