function versionOptFormatter(value, row, index) {
	var content = '<a style="padding:0px 5px;" href="void(0);" onclick="changeVersionState();">状态变更</a>';
	content += '<a style="padding:0px 5px;" href="void(0);" onclick="editVersion();">编辑</a>';
	content += '<a style="padding:0px 5px;" href="void(0);" onclick="deleteVersion();">删除</a>';
	return content;
}

function progressFormatter(value, rowData, rowIndex) {
	var htmlstr = '<div class="easyui-progressbar progressbar easyui-fluid" style="width: 100%; height: 20px;">'
			+ '<div class="progressbar-value" style="width: 100%; height: 20px; line-height: 20px;"> ' + '<div class="progressbar-text" style="width: \'' + value
			+ '%\'; height: 20px; line-height: 20px;">' + value + '%</div>' + '</div>' + '</div>';
	return htmlstr;
}

function onlineFormatter(value) {
	if (value == 0) {
		return '否';
	} else if (value == 1) {
		return '是';
	} else {
		return '';
	}
}

function addAttachment() {

}

function openInsertVersion() {
	$('#win').dialog({
				title : '新建版本',
				width : 600,
				height : '100%',
				href : '${contextPath!}/project/version/form?projectId=' + $('#projectId').val(),
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								var data = $("#versionForm").serializeArray();
								console.log(data);
								$.ajax({
											type : "POST",
											url : '${contextPath!}/project/version/insert',
											data : data,
											success : function(data) {
												if (data.code == 200) {
													$('#versionGrid').datagrid('appendRow', data.data);
													$('#win').dialog('close');
												} else {
													$.messager.alert('错误', data.result);
												}
											},
											error : function() {
												$.messager.alert('错误', '远程访问失败');
											}
										});
							}
						}, {
							text : '取消',
							handler : function() {
								$('#win').dialog('close');
							}
						}]
			});
}

function editVersion() {

}

function deleteVersion() {
}

function changeVersionState() {
}
