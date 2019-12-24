package com.dili.alm.domain;

public enum DemandProjectStatus {

	ASSOCIATED("已关联", 1), NOASSOCIATED("未关联", 2);
	private String name;
	private int value;

	DemandProjectStatus(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

}
