var LogUtils = {
	saveLog : function(content, callback) {
		$.ajax({
			type : "POST",
			url : contextPath + "/logApi/saveLog",
			data : {
				logText : content
			},
			complete : function() {
				callback();
			}
		});
	}
};