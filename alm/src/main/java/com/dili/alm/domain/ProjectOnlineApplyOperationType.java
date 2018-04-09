package com.dili.alm.domain;

public enum ProjectOnlineApplyOperationType {

	PROJECT_MANAGER(5, "项目经理"), TEST_MANAGER(1, "测试经理"), OPERATION_MANAGER(2, "运维负责人"), OPERATION_EXECUTOR(3,
			"运维上线责任人"), VERIFIER(4, "验证人");

	private Integer value;
	private String name;

	private ProjectOnlineApplyOperationType(Integer value, String name) {
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
