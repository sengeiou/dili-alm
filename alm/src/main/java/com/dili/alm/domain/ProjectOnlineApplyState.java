package com.dili.alm.domain;

public enum ProjectOnlineApplyState {

	APPLING(1, "申请中"), TESTER_CONFIRING(2, "测试确认中"), EXECUTING(3, "执行中"), VARIFING(4, "验证中"), COMPLETED(5, "上线成功"), FAILURE(-1,
			"上线失败");

	private Integer value;
	private String name;

	public static ProjectOnlineApplyState valueOf(Integer value) {
		for (ProjectOnlineApplyState state : ProjectOnlineApplyState.values()) {
			if (state.getValue().equals(value)) {
				return state;
			}
		}
		throw new IllegalArgumentException("未知的上线申请状态");
	}

	private ProjectOnlineApplyState(Integer value, String name) {
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
