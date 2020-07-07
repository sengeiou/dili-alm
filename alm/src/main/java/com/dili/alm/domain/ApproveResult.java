package com.dili.alm.domain;

public enum ApproveResult {

	APPROVED(1, "审核通过"), FAILED(0, "审核不通过"), FORWORD(2, "转发");

	private Integer value;
	private String name;

	private ApproveResult(Integer value, String name) {
		this.value = value;
		this.name = name;
	}

	public static ApproveResult valueOf(Integer value) {
		for (ApproveResult result : ApproveResult.values()) {
			if (result.getValue().equals(value)) {
				return result;
			}
		}
		throw new IllegalArgumentException("未知的审核结果");
	}

	public Integer getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

}
