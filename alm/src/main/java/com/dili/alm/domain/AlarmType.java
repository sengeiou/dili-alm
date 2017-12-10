package com.dili.alm.domain;

/**
 * 告警类型
 * 
 * @author jiang
 *
 */
public enum AlarmType {

	PHASE("阶段", "phase"), TASK("任务", "task");

	private String name;
	private String value;

	private AlarmType(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

}
