package com.dili.alm.domain;

public enum TravelCostApplyState {

	APPLING("申请中", 1), REVIEWING("复核中", 2), COMPLETED("已完成", 3);

	private String name;
	private Integer value;

	private TravelCostApplyState(String name, Integer value) {
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
