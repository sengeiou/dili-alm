package com.dili.alm.domain;

public enum IndicatorType {
	BUSINESS("业务类", 1), EFFICIENCY_AND_PERFORMANCE("效率以及性能类", 2);

	private String name;
	private Integer value;

	private IndicatorType(String name, Integer value) {
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
