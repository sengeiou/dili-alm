var isLoadded = false;

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

function onActionCodeChange(nval, oval) {
	if (isLoadded) {
		loadData();
	}
}

function print() {
	$("#gantt").jqprint();
}

function loadData() {
	$('#form').form('submit', {
				url : "${contextPath!}/projectActionRecord/gantt.json",
				success : function(res) {
					var data = $.parseJSON(res);
					if (data.code == "200") {
						$('#gantt').gantt({
									source : data.data,
									itemsPerPage : data.data.length,
									months : ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
									dow : ["S", "M", "T", "W", "T", "F", "S"],
									navigate : 'scroll',
									waitText : '数据加载中......',
									scale : "weeks",
									minScale : "days",
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

function selectAll() {
	var me = $(this);
	$(me.combobox('getData')).each(function(index, item) {
				me.combobox('select', item.code);
			});
	isLoadded = true;
	loadData();
}

function exportExcel() {
	$('#form').form('submit', {
				url : '${contextPath}/projectActionRecord/export',
				success : function(data) {
					var obj = $.parseJSON(data);
					if (obj.code != 200) {
						$.messager.alert('错误', obj.result);
					}
				}
			});
}

$(function() {

		});