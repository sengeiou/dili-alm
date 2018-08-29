// 表格查询
function queryGrid() {
	var opts = $('#grid').datagrid("options");
	if (null == opts.url || "" == opts.url) {
		opts.url = "${contextPath!}/statistical/projectCostStatistic";
	}
	if (!$('#form').form("validate")) {
		return;
	}
	var param = bindMetadata("grid", true);
	var formData = $("#form").serializeObject();
	$.extend(param, formData);
	$('#grid').datagrid("load", param);
}

function exportProjectCost() {
	var projectId = $('#projectId').combobox('getValue');
	var url = '${contextPath!}/statistical/exportProjectCost';
	if (projectId > 0) {
		url += '?projectId=' + projectId;
	}
	window.open(url);
}

$(function() {

			queryGrid();
		});