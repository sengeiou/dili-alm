package com.dili.alm.domain;

public enum WorkOrderOperationType {

	ACCEPTOR(1, "受理人"), EXECUTOR(2, "实施人"), CONFIRM(3, "确认人");

	private Integer value;
	private String name;

	private WorkOrderOperationType(Integer value, String name) {
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
