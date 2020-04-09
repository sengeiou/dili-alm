var gridLoadStep = false;
var originalData;

function onLoadSuccess(data) {
	var grid = $(this);
	if (!gridLoadStep) {
		originalData = data;
		var target = {
			total : 0,
			rows : []
		};
		$(data.rows).each(function(i, ups) {
					if (ups.projectStatistics.length > 0) {
						$(ups.projectStatistics).each(function(i, ps) {
									var obj = {};
									if (i <= 0) {
										obj.userId = ups.userId;
										obj.realName = ups.realName;
										obj.totalWorkHours = ups.totalWorkHours;
									}
									$.extend(true, obj, ps);
									target.total++;
									target.rows.push(obj);
								});
					} else {
						var obj = {
							userId : ups.userId,
							realName : ups.realName
						};
						target.total++;
						target.rows.push(obj);
					}
				});
		gridLoadStep = true;
		grid.datagrid('loadData', target.rows);
	}
	if (gridLoadStep) {
		var idx = 0;
		$(originalData.rows).each(function(i, item) {
					debugger;
					if (item.projectStatistics.length > 0) {
						grid.datagrid('mergeCells', {
									index : idx,
									field : 'realName',
									rowspan : item.projectStatistics.length
								});
						grid.datagrid('mergeCells', {
									index : idx,
									field : 'totalWorkHours',
									rowspan : item.projectStatistics.length
								});
						idx += item.projectStatistics.length;
					} else {
						idx++;
					}
				});
		gridLoadStep = false;
	}
}

function exportUserProjectHoursStatistics() {
	var opts = $("#grid").datagrid("options");
	var param = bindMetadata("grid", true);
	var formData = $("#form").serializeObject();
	var url = '${contextPath}/statistical/exportUserProjectHoursStatistics';
	if (formData) {
		url += '?';
		for (var prop in formData) {
			url += prop + '=' + formData[prop] + '&';
		}
		url = url.substring(0, url.length - 1);
	}
	window.open(url, '_blank');
}

// 清空表单
function clearForm() {
	$('#form').form('reset');
	$('#form').form('clear');
	$('#departmentId').combotree('clear');
	queryGrid();
}
// 表格查询
function queryGrid() {
	var opts = $("#grid").datagrid("options");
	if (null == opts.url || "" == opts.url) {
		opts.url = '${contextPath}/statistical/userProjectHoursStatistics';
	}
	if (!$('#form').form("validate")) {
		return;
	}
	var param = bindMetadata("grid", true);
	var formData = $("#form").serializeObject();
	formData.departmentId = $("#departmentId").val();
	formData.userId = $("#userId").val();
	$("#grid").datagrid("load", formData);
}

function getCheckBoxSelect(row) {

	var opts = $(this).combobox('options');
	return '<input type="checkbox" class="combobox-checkbox">' + row[opts.textField];// 关键在这一步，在项前面加一个checkbox。opts这个是combobox对象。

}

// 打开选择用户弹出框
function selectDep(callback, args) {
	if (callback) {
		eval("(" + callback + "(args))");
	} else {
		showDepDlg($(this)[0].id);
	}
}
function showDepDlg(id) {
	$('#smDialog').dialog({
				title : '部门选择',
				width : 600,
				height : 400,
				href : '${contextPath}/department/departments',
				modal : true,
				buttons : [{
							text : '确定',
							handler : function() {
								confirmDepBtn(id);
							}
						}, {
							text : '取消',
							handler : function() {
								$('#smDialog').dialog('close');
							}
						}]
			});
}

function userNameFormatter(value, row, index) {
	if (!row.userNo) {
		return value;
	}
	return '<a href="${contextPath!}/taskDetails/index?userId=' + row.userNo + '&totalHour=' + row.totalHour + '">' + value + '</a>';
}

function print() {
	$("#dy").jqprint();
}