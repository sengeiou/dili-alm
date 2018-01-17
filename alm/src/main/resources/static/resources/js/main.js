var LogUtils = {
	saveLog : function(module,content, callback) {
		$.ajax({
			type : "POST",
			url : contextPath + "/logApi/saveLog",
			data : {
				logModule:module,
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