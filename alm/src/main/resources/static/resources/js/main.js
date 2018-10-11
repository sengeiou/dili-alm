var LOG_MODULE_OPS = {
	ADD_PROJECT_VERSION : 1,// 新建项目版本
	UPDATE_PROJECT_VERSION : 2,// 修改项目版本
	DELETE_PROJECT_VERSION : 3,// 删除项目版本
	PROJECT_ALARM_CONFIG : 7,// 项目进度告警
	UPDATE_PROJECT_VERSION_STATE : 8,// 变更项目版本状态
	UPLOAD_FILE : 9,// 上传文档
	DELETE_UPLOADED_FILE : 10,// 删除上传文件
	ADD_USER : 11,// 新增用户
	UPDATE_USER : 12,// 修改用户
	DLETE_USER : 13,// 删除用户
	ADD_ROLE : 14,// 新增角色
	UPDATE_ROLE : 15,// 修改角色
	DELETE_ROLE : 16,// 删除角色
	UNBIND_USER_ROLE : 17,// 解除用户角色绑定
	ADD_MENU : 18,// 新增菜单
	UPDATE_MENU : 19,// 修改菜单
	DELETE_MENU : 20,// 删除菜单
	ADD_RESOURCE : 21,// 新增资源
	UPDATE_RESOURCE : 22,// 修改资源
	DELETE_RESOURCE : 23,// 删除资源
	ADD_DEPARTMENT : 24,// 新增部门
	UPDATE_DEPARTMENT : 25,// 修改部门
	DELETE_DEPARTMENT : 26,// 删除部门
	SAVE_WEEKLY : 27,// 保存周报
	SUBMIT_WEEKLY : 28,// 提交周报
	ADD_PROJECT_VERSION_PHASE_TASK : 29,// 新增任务
	UPDATE_PROJECT_VERSION_PHASE_TASK : 30,// 修改任务
	DELETE_PROJECT_VERSION_PHASE_TASK : 31,// 删除任务
	PAUSE_PROJECT_VERSION_PHASE_TASK : 32,// 暂停任务
	START_PROJECT_VERSION_PHASE_TASK : 33,// 开始任务
	PROJECT_APPROVE : 34,// 立项审批
	PROJECT_COMPLETE_APPROVE : 35,// 结项审批
	PROJECT_CHANGE_APPROVE : 36,// 项目变更审批
	ADD_PROJECT_TEAM_MEMBER : 37,// 新增团队成员
	UPDATE_PROJECT_TEAM_MEMBER : 38,// 修改团队成员
	DELETE_PROJECT_TEAM_MEMBER : 39,// 删除团队成员
	ADD_PROJECT_APPLY : 40,// 新增立项申请
	UPDATE_PROJECT_APPLY : 41,// 修改立项申请
	DELETE_PROJECT_APPLY : 42,// 删除立项申请
	SAVE_PROJECT_APPLY : 43,// 保存立项申请
	SUBMIT_PROJECT_APPLY : 44,// 提交立项申请
	ADD_PROJECT_CHANGE : 45,// 新增项目变更申请
	UPDATE_PROJECT_CHANGE : 46,// 修改项目变更申请
	DELETE_PROJECT_CHANGE : 47,// 删除项目变更申请
	ADD_PROJECT_COMPLETE : 48,// 新增结项申请
	UPDATE_PROJECT_COMPLETE : 49,// 修改结项申请
	DELETE_PROJECT_COMPLETE : 50,// 删除结项申请
	SAVE_PROJECT_COMPLETE : 51,// 保存结项申请
	SUBMIT_PROJECT_COMPLETE : 52, // 提交结项申请
	PERFORM_PROJECT_VERSION_PHASE_TASK : 53,// 执行任务
	COMPLETE_PROJECT_VERSION_PHASE_TASK : 54
// 完成任务
};

var LogUtils = {
	saveLog : function(module, content, callback) {
		$.ajax({
			type : "POST",
			url : contextPath + "/logApi/saveLog",
			data : {
				logModule : module,
				logText : content
			},
			complete : function() {
				callback();
			}
		});
	}
};

$(function() {
	var buttons = $.extend([], $.fn.datebox.defaults.buttons);
	buttons.splice(1, 0, {
		text : '清除',
		handler : function(target) {
			$(target).datebox("setValue", "");
		}
	});
	$('.easyui-datebox').datebox({
		buttons : buttons
	});

});