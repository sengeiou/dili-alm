package com.dili.alm.domain;

public enum ProjectCostType {
	HUMAN_COST("人天成本", 1), SOFTWARE_COST("软件成本", 2), HARDWARE_PHYSICAL_MACHINE_COST("硬件成本-实体机",
			3), HARDWARE_VIRTUAL_MACHINE_COST("硬件成本-虚拟机",
					4), TRAVEL_CONFERENCE_COST("差旅会议成本", 5), OTHER_COST("其他成本", 6);

	private String name;
	private Integer value;

	private ProjectCostType(String name, Integer value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Integer getValue() {
		return value;
	}

}
