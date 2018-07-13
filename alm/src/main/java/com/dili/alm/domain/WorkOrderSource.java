package com.dili.alm.domain;

public enum WorkOrderSource {

	OUTSIDE("外部工单", 1), DEPARTMENT("部门工单", 2), DEVELOPMENT_CENTER("研发中心工单", 3);

	private String name;
	private Integer value;

	public static WorkOrderSource intValueOf(Integer value) {
		for (WorkOrderSource source : WorkOrderSource.values()) {
			if (source.getValue().equals(value)) {
				return source;
			}
		}
		throw new IllegalArgumentException("未知的工单来源");
	}

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
