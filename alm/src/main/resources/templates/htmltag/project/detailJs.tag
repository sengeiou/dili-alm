function countVersionGrid() {
	var data = $('#versionGrid').datagrid('getRows').length;
	$('#versionCount').text('（' + data + '条版本记录）');
}

function countPhaseGrid() {
	var data = $('#phaseGrid').datagrid('getRows').length;
	$('#phaseCount').text('（' + data + '条阶段记录）');
}

function countFileGrid() {
	var data = $('#fileGrid').datagrid('getRows').length;
	$('#fileCount').text('（' + data + '条）');
}

function showTask() {
	var backUrl = encodeURIComponent(window.location)
	var location = '${contextPath!}/task/index.html?projectId=${model.id!}' + '&backUrl=' + backUrl;
	window.location.href = location;
}

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
	var progress = value;
	if (value > 100) {
		progress = 100;
	}
	var htmlstr = '<div style="width: 100px; height:20px;border: 1px solid #299a58;"><div style="width:' + progress + 'px; height:20px; background-color: #299a58;"><span>' + value
			+ '%</span></div></div>';
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
	var content = '';
	var projectMember = $('#projectMember').val() == 'true';
	if (projectMember) {
		content += '<a style="padding:0px 5px;" href="javascript:void(0);" onclick="downloadFile(' + row.id + ');">下载</a>';
	}
	var projectManager = $('#projectManager').val() == 'true';
	var editable = $('#editable').val() == 'true';
	if ((editable && projectManager) || row.$_createMemberId == $('#loginUserId').val()) {
		content += '<a style="padding:0px 5px;" href="javascript:void(0);" onclick="deleteFile(' + row.id + ');">删除</a>';
	}
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
									$('#fileGrid').datagrid('acceptChanges');
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
													var rowIndex = $('#fileGrid').datagrid('getRowIndex', item);
													$('#fileGrid').datagrid('deleteRow', rowIndex);
													$('#fileGrid').datagrid('acceptChanges');
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
													var rowIndex = $('#fileGrid').datagrid('getRowIndex', item);
													$('#fileGrid').datagrid('deleteRow', rowIndex);
													$('#fileGrid').datagrid('acceptChanges');
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
				height : 500,
				href : '${contextPath!}/project/version/add?projectId=' + $('#projectId').val(),
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								if (!$('#versionForm').form('validate')) {
									return;
								}
								var data = $("#versionForm").serializeArray();
								$.ajax({
											type : "POST",
											url : '${contextPath!}/project/version/insert',
											data : data,
											success : function(data) {
												if (data.code == 200) {
													try {
														LogUtils.saveLog(LOG_MODULE_OPS.ADD_PROJECT_VERSION, "新增项目版本:" + data.data.id+":"+data.data.version+":成功", function() {
																});
													} catch (e) {
														$.messager.alert('错误', e);
													}
													$('#versionGrid').datagrid('appendRow', data.data);
													$('#versionGrid').datagrid('acceptChanges');
													countVersionGrid();
													if ($('#versionForm input[name=fileIds]').length > 0) {
														$('#fileGrid').datagrid('reload');
														countFileGrid();
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
				height : 500,
				href : '${contextPath!}/project/version/edit?id=' + id,
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								if (!$('#versionForm').form('validate')) {
									return;
								}
								var data = $("#versionForm").serializeArray();
								$.ajax({
											type : "POST",
											url : '${contextPath!}/project/version/update',
											data : data,
											success : function(res) {
												if (res.code == 200) {
													try {
														LogUtils.saveLog(LOG_MODULE_OPS.UPDATE_PROJECT_VERSION, "修改项目版本:" + res.data.id+":"+res.data.version+":成功", function() {
																});
													} catch (e) {
														$.messager.alert('错误', e);
													}
													var row = $('#versionGrid').datagrid('getSelected');
													var index = $('#versionGrid').datagrid('getRowIndex', row);
													$('#versionGrid').datagrid('updateRow', {
																index : index,
																row : res.data
															});
													$('#versionGrid').datagrid('acceptChanges');
													countVersionGrid();
													if ($('#versionForm input[name=fileIds]').length > 0) {
														$('#fileGrid').datagrid('reload');
														countFileGrid();
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
									try {
										LogUtils.saveLog(LOG_MODULE_OPS.DELETE_PROJECT_VERSION, "删除项目版本:" + data.data+":成功", function() {
												});
									} catch (e) {
										$.messager.alert('错误', e);
									}
									var delRow = $('#versionGrid').datagrid('getSelected');
									var index = $('#versionGrid').datagrid('getRowIndex', delRow);
									$($('#fileGrid').datagrid('getRows')).each(function(i, item) {
												if (item.$_versionId == delRow.id) {
													var rowIndex = $('#fileGrid').datagrid('getRowIndex', item);
													$('#fileGrid').datagrid('deleteRow', rowIndex);
												}
											});
									$('#fileGrid').datagrid('acceptChanges');
									countFileGrid();
									$('#versionGrid').datagrid('deleteRow', index);
									$('#versionGrid').datagrid('acceptChanges');
									countVersionGrid();
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
				height : 500,
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
													try {
														LogUtils.saveLog(LOG_MODULE_OPS.UPDATE_PROJECT_VERSION_STATE, "变更项目版本状态:" + data.data.id+":"+data.data.name+":成功", function() {
																});
													} catch (e) {
														$.messager.alert('错误', e);
													}
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
				height : 500,
				href : '${contextPath!}/project/phase/add?projectId=' + $('#projectId').val(),
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								if (!$('#phaseForm').form('validate')) {
									return;
								}
								var data = $("#phaseForm").serializeArray();
								$.ajax({
											type : "POST",
											url : '${contextPath!}/project/phase/insert',
											data : data,
											success : function(data) {
												if (data.code == 200) {
													try {
														LogUtils.saveLog(LOG_MODULE_OPS.ADD_PROJECT_VERSION_PHASE, "新增项目版本阶段:" + data.data.id+":"+data.data.name+":成功", function() {
																});
													} catch (e) {
														$.messager.alert('错误', e);
													}
													$('#phaseGrid').datagrid('appendRow', data.data);
													$('#phaseGrid').datagrid('acceptChanges');
													countPhaseGrid();
													if ($('#phaseForm input[name=fileIds]').length > 0) {
														$('#fileGrid').datagrid('reload');
														countFileGrid();
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
				title : '编辑阶段',
				width : 600,
				height : 500,
				href : '${contextPath!}/project/phase/edit?id=' + id,
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								if (!$('#phaseForm').form('validate')) {
									return;
								}
								var data = $("#phaseForm").serializeArray();
								$.ajax({
											type : "POST",
											url : '${contextPath!}/project/phase/update',
											data : data,
											success : function(data) {
												if (data.code == 200) {
													try {
														LogUtils.saveLog(LOG_MODULE_OPS.UPDATE_PROJECT_VERSION, "编辑项目版本阶段:" + data.data.id+":"+data.data.name+":成功", function() {
																});
													} catch (e) {
														$.messager.alert('错误', e);
													}
													var row = $('#phaseGrid').datagrid('getSelected');
													var index = $('#phaseGrid').datagrid('getRowIndex', row);
													$('#phaseGrid').datagrid('updateRow', {
																index : index,
																row : data.data
															});
													$('#phaseGrid').datagrid('acceptChanges');
													if ($('#phaseForm input[name=fileIds]').length > 0) {
														$('#fileGrid').datagrid('reload');
														countFileGrid();
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
								console.log(data.data);
									try {
										LogUtils.saveLog(LOG_MODULE_OPS.DELETE_PROJECT_VERSION_PHASE, "删除项目版本版本阶段:" +data.data.id+":"+data.data.name+":成功", function() {
												});
									} catch (e) {
										$.messager.alert('错误', e);
									}
									var delRow = $('#phaseGrid').datagrid('getSelected');
									var index = $('#phaseGrid').datagrid('getRowIndex', delRow);
									$($('#fileGrid').datagrid('getRows')).each(function(i, item) {
												if (item.$_phaseId == delRow.id) {
													var rowIndex = $('#fileGrid').datagrid('getRowIndex', item);
													$('#fileGrid').datagrid('deleteRow', rowIndex);
												}
											});
									$('#fileGrid').datagrid('acceptChanges');
									countFileGrid();
									$('#phaseGrid').datagrid('deleteRow', index);
									$('#phaseGrid').datagrid('acceptChanges');
									countPhaseGrid();
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

function uploadFile(projectId) {
	$('#win').dialog({
				title : '新建文档',
				width : 600,
				height : 500,
				href : '${contextPath!}/project/uploadFileView?projectId=' + projectId,
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								if (!$('#uploadForm').form('validate')) {
									return;
								}
								if ($('#uploadFileGrid').datagrid('getRows').length <= 0) {
									$.messager.alert('错误', '请上传文件');
									return;
								}
								var data = $("#uploadForm").serializeArray();
								$.ajax({
											type : "POST",
											url : '${contextPath!}/project/uploadFile',
											data : data,
											success : function(data) {
												if (data.code == 200) {
													try {
														LogUtils.saveLog(LOG_MODULE_OPS.UPLOAD_FILE, "上传项目文档:" + data.data.name+":成功", function() {
																});
													} catch (e) {
														$.messager.alert('错误', e);
													}
													$(data.data).each(function(index, item) {
																$('#fileGrid').datagrid('appendRow', item);
																$('#fileGrid').datagrid('acceptChanges');
															});
													countFileGrid()
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

function showMembers() {
	var href = '${contextPath!}/team/index.html?projectId=${model.id!}';
	if ($('#editable').val() == 'true') {
		href += '&editable=true';
	}
	window.location.href = href;
}

function alarmConfig() {
	$('#win').dialog({
				title : '告警设置',
				width : 600,
				height : 500,
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
													try {
														LogUtils.saveLog(LOG_MODULE_OPS.PROJECT_ALARM_CONFIG, "设置项目进度告警:" + data.result+":成功", function() {
																});
													} catch (e) {
														$.messager.alert('错误', e);
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

function getMyDay() {
	 var  htmlobj=$.ajax({url:"${contextPath!}/workDay/isWorkEndDayDate",async:false});
     var str=htmlobj.responseText;
     var obj = $.parseJSON(str);
     return obj;
}

function generateWeekly() {
	console.log(getMyDay());
	if (getMyDay()==0) {
		$.messager.alert('不能提交周报', ' 不是工作日最后一天 不可提交周报');
		return false;
	}else if(getMyDay()==1){
		$.messager.alert('不能提交周报', ' 今天不是工作日');
		return false;
	}

	window.location.href = '${contextPath}/weekly/getDescAddByProjectId?projectId=' + $('#projectId').val();
}