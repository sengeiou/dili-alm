var LOG_MODULE_OPS = {
	ADD_PROJECT_VERSION : 1,// 新建项目版本
	UPDATE_PROJECT_VERSION : 2,// 修改项目版本
	DELETE_PROJECT_VERSION : 3,// 删除项目版本
	ADD_PROJECT_VERSION_PHASE : 4,// 新建项目版本阶段
	UPDATE_PROJECT_VERSION_PHASE:5,//修改项目版本阶段
	DELETE_PROJECT_VERSION_PHASE:6,//删除项目版本阶段
	
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