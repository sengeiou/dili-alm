package com.dili.alm.domain;

public enum ProjectState {

	NOT_START("未开始", 0), IN_PROGRESS("进行中", 1), PAUSED("暂停中", 3), CLOSED("已关闭", 4);

	private String name;
	private Integer value;

	private ProjectState(String name, Integer value) {
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
