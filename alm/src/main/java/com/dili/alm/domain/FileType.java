package com.dili.alm.domain;

public enum FileType {

	PRODUCT("产品类文档", 1), DEVELOPMENT("开发类文档", 2), TEST("测试类文档", 3), PROJECT_INTRODUCE("项目介绍类文档",
			4), ONLINE_INTRODUCE("上线部署说明文档", 5), APPLY("立项文档", 6), ORTHER("其他文档", 0);

	private String name;
	private int value;

	FileType(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

}
