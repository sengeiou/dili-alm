package com.dili.alm.domain;

public enum OperationResult {

	SUCCESS(1, "执行成功"), FAILURE(0, "执行失败");

	private Integer value;
	private String name;

	private OperationResult(Integer value, String name) {
		this.value = value;
		this.name = name;
	}

	public static OperationResult valueOf(Integer value) {
		for (OperationResult result : OperationResult.values()) {
			if (result.getValue().equals(value)) {
				return result;
			}
		}
		throw new IllegalArgumentException("未知的操作结果");
	}

	public Integer getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

}
