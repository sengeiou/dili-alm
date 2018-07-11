package com.dili.alm.domain;

public enum ProjectAction {

	PROJECT_PLAN("项目规划", "project_plan", ActionDateType.PERIOD), VERSION_PLAN("版本规划", "version_plan",
			ActionDateType.PERIOD), TASK_PLAN("任务规划", "task_plan", ActionDateType.PERIOD), PROJECT_APPLY("立项申请",
					"project_apply", ActionDateType.POINT), PROJECT_APPLY_APPROVE("立项审批", "project_approve",
							ActionDateType.POINT), PROJECT_START("项目开始", "project_start",
									ActionDateType.POINT), VERSION_START("版本开始", "version_start",
											ActionDateType.POINT), VERSION_PAUSE("版本暂停", "version_pause",
													ActionDateType.POINT), VERSION_RESUME("版本重启", "version_resume",
															ActionDateType.POINT), VERSION_COMPLETE("版本完成",
																	"version_complete",
																	ActionDateType.POINT), PROJECT_CHANGE_APPLY("变更申请",
																			"project_change_apply",
																			ActionDateType.POINT), PROJECT_CHANGE_APPROVE(
																					"变更审批", "project_change_approve",
																					ActionDateType.POINT), VERSION_ONLINE_APPLY(
																							"版本上线申请",
																							"version_online_apply",
																							ActionDateType.POINT), VERSION_ONLINE(
																									"版本上线",
																									"version_online",
																									ActionDateType.POINT), PROJECT_COMPLETE_APPLY(
																											"结项申请",
																											"project_complete_apply",
																											ActionDateType.POINT), PROJECT_COMPLETE_APPROVE(
																													"结项审批",
																													"project_complete_approve",
																													ActionDateType.POINT), PROJECT_COMPLETE(
																															"项目结束",
																															"project_complete",
																															ActionDateType.POINT);

	private String name;
	private String code;
	private ActionDateType dateType;

	private ProjectAction(String name, String code, ActionDateType dateType) {
		this.name = name;
		this.code = code;
		this.dateType = dateType;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public ActionDateType getDateType() {
		return dateType;
	}

}
