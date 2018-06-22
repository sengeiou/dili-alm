$(function() {
			$('#gantt').gantt({
						source : "${contextPath!}/projectActionRecord/gantt.json?projectId=23",
						months : ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
						// dow : ["S", "M", "T", "W", "T", "F", "S"],
						waitText : '数据加载中......',
						scale : "weeks",
						minScale : "weeks",
						maxScale : "months",
						onRender : function() {
						}
					});
		});