package com.dili.alm.domain;

public enum ActionDateType {

	PERIOD(1), POINT(2);

	private Integer value;

	private ActionDateType(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

}
