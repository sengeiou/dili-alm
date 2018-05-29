package com.dili.alm.domain;

/**
 * 工单状态
 * 
 * @author jiang
 *
 */
public enum WorkOrderState {

	APPLING(1, "申请中"), OPENED(2, "开启"), SOLVING(3, "解决中"), SOLVED(4, "已解决"), CLOSED(5, "已关闭");

	private Integer value;
	private String name;

	private WorkOrderState(Integer value, String name) {
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
