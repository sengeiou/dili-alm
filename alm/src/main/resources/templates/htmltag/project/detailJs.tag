function versionOptFormatter(value, row, index) {
	var content = '<a style="padding:0px 5px;" href="javascript:void(0);" onclick="changeVersionState(' + row.id + ');">状态变更</a>';
	content += '<a style="padding:0px 5px;" href="javascript:void(0);" onclick="editVersion(' + row.id + ');">编辑</a>';
	content += '<a style="padding:0px 5px;" href="javascript:void(0);" onclick="deleteVersion(' + row.id + ');">删除</a>';
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

function versionFileOptFormatter(value, row, index) {
	return '<a href="javascript:void(0);" onclick="delFile(' + row.id + ');">删除</a>';
}

function onFileGridLoadSuccess(data) {
}

function delFile(id) {
	$.messager.confirm('提示', '确定要删除该文档？', function(flag) {
				if (!flag) {
					return false;
				}
				$.ajax({
							type : "POST",
							url : '${contextPath!}/files/delete',
							data : {
								id : id
							},
							success : function(data) {
								if (data.code == 200) {
									var index = $('#versionFileGrid').datagrid('getRowIndex', $('#versionFileGrid').datagrid('getSelected'));
									$('#versionFileGrid').datagrid('deleteRow', index);
									$('input[name=fileIds][value=' + id + ']').remove();
								} else {
									$.messager.alert('错误', data.result);
								}
							},
							error : function() {
								$.messager.alert('错误', '远程访问失败');
							}
						});
			});
}

function openInsertVersion() {
	$('#win').dialog({
				title : '新建版本',
				width : 600,
				height : '100%',
				href : '${contextPath!}/project/version/add?projectId=' + $('#projectId').val(),
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								var data = $("#versionForm").serializeArray();
								$.ajax({
											type : "POST",
											url : '${contextPath!}/project/version/insert',
											data : data,
											success : function(data) {
												if (data.code == 200) {
													$('#versionGrid').datagrid('appendRow', data.data);
													$('#versionGrid').datagrid('acceptChanges');
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

function editVersion(id) {
	$('#win').dialog({
				title : '编辑版本',
				width : 600,
				height : '100%',
				href : '${contextPath!}/project/version/edit?id=' + id + '&projectId=' + $('#projectId').val(),
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								var data = $("#versionForm").serializeArray();
								$.ajax({
											type : "POST",
											url : '${contextPath!}/project/version/update',
											data : data,
											success : function(data) {
												if (data.code == 200) {
													debugger;
													var row = $('#versionGrid').datagrid('getSelected');
													var index = $('#versionGrid').datagrid('getRowIndex', row);
													$('#versionGrid').datagrid('updateRow', {
																index : index,
																row : data.data
															});
													$('#versionGrid').datagrid('acceptChanges');
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

function deleteVersion(id) {
	$.messager.confirm('提示', '确定要删除该版本？', function(flag) {
				if (!flag) {
					return false;
				}
				$.ajax({
							type : "POST",
							url : '${contextPath!}/project/version/delete',
							data : {
								id : id
							},
							success : function(data) {
								if (data.code == 200) {
									var index = $('#versionGrid').datagrid('getRowIndex', $('#versionGrid').datagrid('getSelected'));
									$('#versionGrid').datagrid('deleteRow', index);
									$('#versionGrid').datagrid('acceptChanges');
								} else {
									$.messager.alert('错误', data.result);
								}
							},
							error : function() {
								$.messager.alert('错误', '远程访问失败');
							}
						});
			});
}

function changeVersionState(id) {
	$('#win').dialog({
				title : '状态变更',
				width : 600,
				height : '100%',
				href : '${contextPath!}/project/version/changeState?id=' + id,
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								var versionState = $("#versionState").combobox('getValue');
								$.ajax({
											type : "POST",
											url : '${contextPath!}/project/version/changeState',
											data : {
												id : id,
												versionState : versionState
											},
											success : function(data) {
												if (data.code == 200) {
													debugger;
													var row = $('#versionGrid').datagrid('getSelected');
													var index = $('#versionGrid').datagrid('getRowIndex', row);
													row.versionState = data.data.versionState;
													row.$_versionState = data.data.$_versionState;
													$('#versionGrid').datagrid('updateRow', {
																index : index,
																row : row
															});
													$('#versionGrid').datagrid('acceptChanges');
													$('#versionGrid').datagrid('refreshRow', index);
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
