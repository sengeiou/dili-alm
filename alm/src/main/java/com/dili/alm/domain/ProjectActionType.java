package com.dili.alm.domain;

public enum ProjectActionType {

	PROJECT(1), VERSION(2);

	private Integer value;

	public Integer getValue() {
		return value;
	}

	private ProjectActionType(Integer value) {
		this.value = value;
	}

}
