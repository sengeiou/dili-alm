package com.dili.alm.domain;

public enum HardwareApplyState {

	APPLING(1, "申请中"), PROJECT_MANAGER_APPROVING(2, "项目经理审批中"), OPERATION_MANAGER_APPROVING(4,
			"运维部经理审批中"), IMPLEMENTING(5, "实施中"), COMPLETED(6, "完成"), FAILED(-1, "失败");

	private Integer value;
	private String name;

	private HardwareApplyState(Integer value, String name) {
		this.value = value;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Integer getValue() {
		return value;
	}

	public static HardwareApplyState valueOf(Integer value) {
		for (HardwareApplyState state : HardwareApplyState.values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		throw new IllegalArgumentException("未知IT资源申请状态");
	}

}
