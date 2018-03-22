package com.dili.alm.domain;

public enum HardwareResourceApplyState {

	APPLING("申请中", 1), APPROVING("审批中", 2), IMPLEMENTING("实施中", 3), COMPLETED("已完成", 4);

	private String name;
	private Integer value;

	private HardwareResourceApplyState(String name, Integer value) {
		this.name = name;
		this.value = value;
	}

	public static HardwareResourceApplyState valueOf(Integer value) {
		for (HardwareResourceApplyState state : HardwareResourceApplyState.values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		throw new IllegalArgumentException("未知的资源申请状态");
	}

	public String getName() {
		return name;
	}

	public Integer getValue() {
		return value;
	}

}
