package com.dili.alm.constant;

public class BpmConsts {
	
	public enum HardwareApplyConstant {
		HARDWARE_APPLY_CODE("HardwareApplyId"), 
		PROCESS_DEFINITION_KEY("HardwareResourceApplyProcess");

		private String name;

		private HardwareApplyConstant(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	/**
	 * 流程变量中的需求单号
	 */
	public static final String DEMAND_CODE = "demandCode";
	/**
	 * 流程标识，用于启动流程
	 */
	public static final String PROCESS_DEFINITION_KEY = "almDemandProcess";

	/**
	 * 立项流程标识，用于启动流程
	 */
	public static final String PROJECT_APPLY_PROCESS = "almProjectApplyProcess";
	/**
	 * 变更流程标识，用于启动流程
	 */
	public static final String PROJECT_CHANGE_PROCESS = "almProjectChangeProcess";
	/**
	 * 结项流程标识，用于启动流程
	 */
	public static final String PROJECT_COMPLETE_PROCESS = "almProjectCompleteProcess";

	public enum ProjectOnlineApplyConstant {
		PROCESS_KEY("almProjectOnlineApplyProcess"), 
		BUSINESS_KEY("serialNumber"), 
		PROJECT_MANAGER_KEY("projectManager"), 
		PRODUCT_MANAGER_KEY("productManager"), 
		EXECUTOR_KEY("executorId"),
		APPLICANT_KEY("applicantId");

		private String name;

		private ProjectOnlineApplyConstant(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}