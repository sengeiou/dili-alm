var LogUtils = {
	saveLog : function(content, callback) {
		$.ajax({
			type : "POST",
			url : "http://alm.diligrp.com:8083/alm/logApi/saveLog",
			data : {
				logText : content
			},
			xhrFields : {
				withCredentials : true
			},
			crossDomain : true,
			complete : function() {
				callback();
			}
		});
	}
};

//$.fn.combobox.defaults.icons = [ {
//	iconCls : 'icon-clear',
//	handler : function(e) {
//		$(e.handleObj.data.target).combobox('clear');
//	}
//} ];

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