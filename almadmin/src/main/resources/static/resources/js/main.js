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