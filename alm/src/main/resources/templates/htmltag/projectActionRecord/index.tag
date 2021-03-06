var isLoadded = false;

function multicomboboxFormatter(row) {
	var opts = $(this).combobox('options');
	return '<input type="checkbox" class="combobox-checkbox" id="' + row[opts.valueField] + '">' + row[opts.textField]
}

function checkActionCode(nval, oval) {
	nval.minus(oval);
}

function onActionCodeSelected(record) {
	$('#' + record.code).prop('checked', true);
}

function onActionCodeUnselected(record) {
	$('#' + record.code).prop('checked', false);
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
									itemsPerPage : 15,
									months : ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
									dow : ["S", "M", "T", "W", "T", "F", "S"],
									navigate : 'scroll',
									waitText : '数据加载中......',
									scale : "days",
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

function initActionCode() {
	if (isLoadded) {
		return;
	}
	isLoadded = true;
	var data = $(this).combobox('getData');
	$(this).combobox('loadData', data);
	selectAll.call($(this));
	loadData();
}

function unSelectAll() {
	var me = $(this);
	$(me.combobox('getData')).each(function(index, item) {
				me.combobox('unselect', item.code);
			});
}

function selectAll() {
	var me = $(this);
	$(me.combobox('getData')).each(function(index, item) {
				me.combobox('select', item.code);
			});
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

function selectOrUnselectAll() {
}

$(function() {
		});