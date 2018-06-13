package com.dili.alm.domain;

public enum ProjectAction {

	PROJECT_PLAN("项目规划", "project_plan"), VERSION_PLAN("版本规划", "version_plan"), TASK_PLAN("任务规划",
			"task_plan"), PROJECT_APPLY("立项申请", "project_apply"), PROJECT_APPLY_APPROVE("立项审批",
					"project_approve"), PROJECT_START("项目开始", "project_start"), VERSION_START("版本开始",
							"version_start"), VERSION_PAUSE("版本暂停", "version_pause"), VERSION_RESUME("版本重启",
									"version_resume"), VERSION_COMPLETE("版本完成",
											"version_complete"), PROJECT_CHANGE_APPLY("变更申请",
													"project_change_apply"), PROJECT_CHANGE_APPROVE("变更审批",
															"project_change_approve"), VERSION_ONLINE_APPLY("版本上线申请",
																	"version_online_apply"), VERSION_ONLINE("版本上线",
																			"version_online"), PROJECT_COMPLETE_APPLY(
																					"结项申请",
																					"project_complete_apply"), PROJECT_COMPLETE_APPROVE(
																							"结项审批",
																							"project_complete_apply"), PROJECT_COMPLETE(
																									"项目结束",
																									"project_complete");

	private String name;
	private String code;

	private ProjectAction(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

}
