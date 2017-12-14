package com.dili.alm.domain;

public enum TeamRole {

	PROJECT_MANAGER("项目负责人", "project_manager"), PRODUCT_MANAGER("产品经理", "product_manager"), TEST_MANAGER("测试经理",
			"test_manager"), DEVELOP_MANAGER("开发经理", "develop_manager"), TESTER("测试人员", "tester"), DEVELOPER("开发人员",
					"developer"), ARTIST("美工", "artist"), INTERACTIVITY("交互", "interactivity");

	private String name;
	private String value;

	private TeamRole(String name, String value) {
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
