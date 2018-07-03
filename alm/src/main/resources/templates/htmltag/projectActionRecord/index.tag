function multicomboboxFormatter(row) {
	var opts = $(this).combobox('options');
	return '<input type="checkbox" class="combobox-checkbox" id="' + row[opts.valueField] + '">' + row[opts.textField]
}

function onActionCodeSelected(record) {
	$('#' + record.code).prop('checked', true);
}

function onActionCodeUnselected(record) {
	$('#' + record.code).prop('checked', false);
}

function loadData(data) {
	$.ajax({
				type : "POST",
				url : "${contextPath!}/projectActionRecord/gantt.json",
				data : data,
				processData : true,
				dataType : "json",
				async : true,
				success : function(data) {
					if (data.code == "200") {
						$('#gantt').gantt({
									source : data.data,
									months : ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
									// dow : ["S", "M", "T", "W", "T",
									// "F", "S"],
									waitText : '数据加载中......',
									scale : "weeks",
									minScale : "weeks",
									maxScale : "months",
									onRender : function() {
									}
								});
					} else {
						$.messager.alert('错误', data.result);
					}
				},
				error : function() {
					$.messager.alert('错误', '远程访问失败');
				}
			});
}

$(function() {
			loadData();
		});