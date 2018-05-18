package com.dili.alm.domain;

public enum ProjectVersionState {

	NOT_START("未开始", 0), COMPLETED("已完成", 2), IN_PROGRESS("进行中", 1), PAUSED("暂停中", 3), CLOSED("已关闭", 4);

	private String name;
	private Integer value;

	private ProjectVersionState(String name, Integer value) {
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
