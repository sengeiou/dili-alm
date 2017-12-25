// 打开选择用户弹出框
function selectDep(callback, args) {
	if (callback) {
		eval("(" + callback + "(args))");
	} else {
		showDepDlg($(this)[0].id);
	}
}
// 确认选择事件
function confirmDepBtn(id) {
	var selected = $('#smGridList').datagrid('getSelected');
	$('#' + id).textbox('initValue', selected.id);
	$('#' + id).textbox('setText', selected.name);
	$('#smDialog').dialog('close');
}
// 根据id打开用户选择
function showDepDlg(id) {
	$('#smDialog').dialog({
				title : '部门选择',
				width : 600,
				height : 400,
				queryParams : {
					textboxId : id
				},
				href : '${contextPath!}/department/departments.html',
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