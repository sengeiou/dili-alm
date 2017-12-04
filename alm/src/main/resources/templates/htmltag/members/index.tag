// 打开选择用户弹出框
function selectMember(callback, args) {
	if (callback) {
		eval("(" + callback + "(args))");
	} else {
		showMembersDlg($(this)[0].id);
	}
}
// 确认选择事件
function confirmMembersBtn(id) {
	var selected = $('#smGridList').datagrid('getSelected');
	$('#' + id).textbox('initValue', selected.id);
	$('#' + id).textbox('setText', selected.realName);
	$('#smDialog').dialog('close');
}
// 根据id打开用户选择
function showMembersDlg(id) {
	$('#smDialog').dialog({
				title : '用户选择',
				width : 800,
				height : 400,
				queryParams : {
					textboxId : id
				},
				href : '${contextPath!}/member/members.html',
				modal : true,
				buttons : [{
							text : '确定',
							handler : function() {
								confirmMembersBtn(id);
							}
						}, {
							text : '取消',
							handler : function() {
								$('#smDialog').dialog('close');
							}
						}]
			});
}