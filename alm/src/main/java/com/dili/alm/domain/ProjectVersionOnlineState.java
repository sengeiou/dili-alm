package com.dili.alm.domain;

public enum ProjectVersionOnlineState {

	ONLINE(1, "已上线"), NOT_ONLINE(0, "未上线");

	private Integer value;
	private String name;

	private ProjectVersionOnlineState(Integer value, String name) {
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
