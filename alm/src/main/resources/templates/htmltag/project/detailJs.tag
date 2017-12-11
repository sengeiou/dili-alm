function versionOptFormatter(value, row, index) {
	var content = '<a style="padding:0px 5px;" href="javascript:void(0);" onclick="changeVersionState(' + row.id + ');">状态变更</a>';
	content += '<a style="padding:0px 5px;" href="javascript:void(0);" onclick="editVersion(' + row.id + ');">编辑</a>';
	content += '<a style="padding:0px 5px;" href="javascript:void(0);" onclick="deleteVersion(' + row.id + ');">删除</a>';
	return content;
}

function phaseOptFormatter(value, row, index) {
	var content = '<a style="padding:0px 5px;" href="javascript:void(0);" onclick="editPhase(' + row.id + ');">编辑</a>';
	content += '<a style="padding:0px 5px;" href="javascript:void(0);" onclick="deletePhase(' + row.id + ');">删除</a>';
	return content;
}

function dateFormatter(value, row, index) {
	if (value) {
		var date = new Date();
		date.setTime(value);
		return date.Format('yyyy-MM-dd HH:mm:ss');
	}
	return '';
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
	return '<a href="javascript:void(0);" onclick="delVersionFile(' + row.id + ');">删除</a>';
}

function phaseFileOptFormatter(value, row, index) {
	return '<a href="javascript:void(0);" onclick="delPhaseFile(' + row.id + ');">删除</a>';
}

function fileOptFormatter(value, row, index) {
	var content = '<a style="padding:0px 5px;" href="javascript:void(0);" onclick="downloadFile(' + row.id + ');">下载</a>';
	content += '<a style="padding:0px 5px;" href="javascript:void(0);" onclick="deleteFile(' + row.id + ');">删除</a>';
	return content;
}

function downloadFile(id) {
	window.open('${contextPath!}/files/download?id=' + id);
}

function deleteFile(id) {
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
									var selected = $('#fileGrid').datagrid('getSelected');
									var index = $('#fileGrid').datagrid('getRowIndex', selected);
									$('#fileGrid').datagrid('deleteRow', index);
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

function delVersionFile(id) {
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
									var selected = $('#versionFileGrid').datagrid('getSelected');
									var index = $('#versionFileGrid').datagrid('getRowIndex', selected);
									$('#versionFileGrid').datagrid('deleteRow', index);
									$($('#fileGrid').datagrid('getRows')).each(function(i, item) {
												if (item.id == selected.id) {
													$('#fileGrid').datagrid('deleteRow', i);
													return false;
												}
											});
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

function delPhaseFile(id) {
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
									var selected = $('#phaseFileGrid').datagrid('getSelected');
									var index = $('#phaseFileGrid').datagrid('getRowIndex', selected);
									$('#phaseFileGrid').datagrid('deleteRow', index);
									$($('#fileGrid').datagrid('getRows')).each(function(i, item) {
												if (item.id == selected.id) {
													$('#fileGrid').datagrid('deleteRow', i);
													return false;
												}
											});
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
													if ($('#versionForm input[name=fileIds]').length > 0) {
														$('#fileGrid').datagrid('reload');
													}
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
											success : function(res) {
												if (res.code == 200) {
													var row = $('#versionGrid').datagrid('getSelected');
													var index = $('#versionGrid').datagrid('getRowIndex', row);
													$('#versionGrid').datagrid('updateRow', {
																index : index,
																row : res.data
															});
													$('#versionGrid').datagrid('acceptChanges');
													if ($('#versionForm input[name=fileIds]').length > 0) {
														$('#fileGrid').datagrid('reload');
													}
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

function openInsertPhase() {
	$('#win').dialog({
				title : '新建阶段',
				width : 600,
				height : '100%',
				href : '${contextPath!}/project/phase/add?projectId=' + $('#projectId').val(),
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								var data = $("#phaseForm").serializeArray();
								$.ajax({
											type : "POST",
											url : '${contextPath!}/project/phase/insert',
											data : data,
											success : function(data) {
												if (data.code == 200) {
													$('#phaseGrid').datagrid('appendRow', data.data);
													$('#phaseGrid').datagrid('acceptChanges');
													if ($('#phaseForm input[name=fileIds]').length > 0) {
														$('#fileGrid').datagrid('reload');
													}
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

function editPhase(id) {
	$('#win').dialog({
				title : '编辑版本',
				width : 600,
				height : '100%',
				href : '${contextPath!}/project/phase/edit?id=' + id,
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								var data = $("#phaseForm").serializeArray();
								$.ajax({
											type : "POST",
											url : '${contextPath!}/project/phase/update',
											data : data,
											success : function(data) {
												if (data.code == 200) {
													var row = $('#phaseGrid').datagrid('getSelected');
													var index = $('#phaseGrid').datagrid('getRowIndex', row);
													$('#phaseGrid').datagrid('updateRow', {
																index : index,
																row : data.data
															});
													$('#phaseGrid').datagrid('acceptChanges');
													if ($('#phaseForm input[name=fileIds]').length > 0) {
														$('#fileGrid').datagrid('reload');
													}
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

function deletePhase(id) {
	$.messager.confirm('提示', '确定要删除该阶段？', function(flag) {
				if (!flag) {
					return false;
				}
				$.ajax({
							type : "POST",
							url : '${contextPath!}/project/phase/delete',
							data : {
								id : id
							},
							success : function(data) {
								if (data.code == 200) {
									var index = $('#phaseGrid').datagrid('getRowIndex', $('#phaseGrid').datagrid('getSelected'));
									$('#phaseGrid').datagrid('deleteRow', index);
									$('#phaseGrid').datagrid('acceptChanges');
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

function showMembers() {
	window.location.href = '${contextPath!}/team/index.html?projectId=${model.id!}';
}

function alarmConfig() {
	$('#win').dialog({
				title : '编辑版本',
				width : 600,
				height : '100%',
				href : '${contextPath!}/alarmConfig/config.html?projectId=' + $('#projectId').val(),
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								var data = $("#alarmForm").serializeArray();
								$.ajax({
											type : "POST",
											url : '${contextPath!}/alarmConfig/saveOrUpdate',
											data : data,
											success : function(data) {
												if (data.code == 200) {
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

function generateWeekly() {
	debugger;
	window.location.href = '${contextPath!}/weekly/getDescAddByProjectId?id=' + $('#projectId').val();
}

$(function() {
			var uploadObj = $("#uploadDiv").uploadFile({
						url : "${contextPath!}/files/filesUpload?projectId=" + $('#projectId').val(), // 文件上传url
						fileName : "file", // 提交到服务器的文件名
						uploadStr : '上传文档',
						// maxFileCount : 1, // 上传文件个数（多个时修改此处
						returnType : 'json', // 服务返回数据
						// allowedTypes : 'jpg,jpeg,png,gif', // 允许上传的文件式
						showDone : false, // 是否显示"Done"(完成)按钮
						showDelete : false, // 是否显示"Delete"(删除)按钮
						showError : false,
						showStatusAfterSuccess : false,
						dragDrop : false,
						onError : function(files, status, errMsg, pd) {
							$.messager.alert('错误', '上传失败！');
						},
						onSuccess : function(files, data, xhr, pd) {
							// 上传成功后的回调方法。本例中是将返回的文件名保到一个hidden类开的input中，以便后期数据处理
							$('#fileGrid').datagrid('reload');
						}
					});
		});