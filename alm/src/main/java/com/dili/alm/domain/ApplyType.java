package com.dili.alm.domain;

public enum ApplyType {

	ONLINE(1, "上线申请");

	private Integer value;
	private String name;

	private ApplyType(Integer value, String name) {
		this.value = value;
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
