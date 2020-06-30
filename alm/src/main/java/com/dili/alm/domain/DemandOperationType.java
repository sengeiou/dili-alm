package com.dili.alm.domain;

public enum DemandOperationType {

	DEMAND_MANAGER(1, "运维审批"), ACCEPT(2, "研发接收需求"), ASSIGN(3, "指定责任人"), RECIPROCATE(4, "责任人对接"),FEEDBACK(5,"记录反馈方案"),DEMAND_ADMIN(6,"需求管理员"),REJECT(7,"需求被驳回"),RESUBMIT(8,"重新提交");

	private Integer value;
	private String name;

	private DemandOperationType(Integer value, String name) {
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
