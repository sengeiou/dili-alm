package com.dili.alm.domain;

public enum WorkOrderSource {

	OUTSIDE("外部工单", 1), DEPARTMENT("内部工单", 2);

	private String name;
	private Integer value;

	private WorkOrderSource(String name, Integer value) {
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
