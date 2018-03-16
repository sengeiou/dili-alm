package com.dili.alm.domain;

/**
 * 工单状态
 * 
 * @author jiang
 *
 */
public enum WorkOrderState {

	APPLING(1, "申请中"), OPENED(2, "开启"), ALLOCATED(3, "已分配"), SOLVING(4, "解决中"), SOLVED(5, "已解决"), CLOSED(6, "已关闭");

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
