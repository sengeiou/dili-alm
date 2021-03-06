var userId = '${user.id!}';
userId = parseInt(userId);

var paramCount = '${subsystemCount!0}';
paramCount = parseInt(paramCount);

function serialNumberFormatter(value, row, index) {
	return '<a href="javascript:void(0);" onclick="detail(' + row.id + ');">' + value + "</a>";
}

function detail(id) {
	$('#win').dialog({
				title : '上线申请详情',
				width : 800,
				height : 540,
				href : '${contextPath!}/projectOnlineApply/detail?id=' + id,
				modal : true,
				buttons : [{
							text : '返回',
							handler : function() {
								$('#win').dialog('close');
							}
						}]
			});
}

function optFormatter(value, row, index) {
	var content = '';
	if (row.editable) {
		if (row.isHandleProcess) {
			content += '<a style="padding:0px 2px;" href="javascript:void(0);" onclick="openUpdate(' + index + ',' + row.id + ',\'' + row.taskId + '\');">编辑</a>';
			content += '<a style="padding:0px 2px;" href="javascript:void(0);" onclick="del(' + row.id + ',' + index + ',\'' + row.taskId + '\');">删除</a>';
		} else {
			content += '<a style="padding:0px 2px;" href="javascript:void(0);" onclick="openUpdate(' + index + ',' + row.id + ');">编辑</a>';
			content += '<a style="padding:0px 2px;" href="javascript:void(0);" onclick="del(' + row.id + ',' + index + ');">删除</a>';
		}
	} else if (row.$_applyState == 1) {
		content += '<span style="padding:0px 2px;">编辑</span>';
		content += '<span style="padding:0px 2px;">删除</span>';
	} else {
		if (row.isHandleProcess) {
			content += '<a href="javascript:void(0);" onclick="handleProcess(\'' + row.taskId + '\',\'' + row.formKey + '\',' + row.isNeedClaim + ');">处理流程</a>';
		} else {
			content += '<span">处理流程</span>';
		}
	}
	return content;

}

function formatMarkets(row) {
	var opts = $(this).combobox('options');
	var val = $(this).combobox('getValue');
	if (row.value == val) {
		return '<input type="checkbox" checked="checked" class="combobox-checkbox">' + row[opts.textField]
	} else {
		return '<input type="checkbox" class="combobox-checkbox">' + row[opts.textField];
	}
}

function checkMarketComboState(nval, oval) {
	if (nval == 1) {
		$('#market').textbox('enable');
	} else {
		$('#market').textbox('disable');
	}
}

function downloadFile(id) {
	window.open('${contextPath!}/files/download?id=' + id);
}

function handleProcess(taskId, formKey, isNeedClaim) {
	$.ajax({
				type : "POST",
				url : '${contextPath!}/process/getTaskUrl',
				data : {
					taskId : taskId,
					formKey : formKey,
					isNeedClaim : isNeedClaim
				},
				processData : true,
				dataType : "json",
				async : true,
				success : function(data) {
					if (data.success) {
						window.location.href = data.data.taskUrl + '?taskId=' + taskId + '&isNeedClaim=' + isNeedClaim;
					} else {
						$.messager.alert('错误', data.result);
					}
				},
				error : function() {
					$.messager.alert('错误', '远程访问失败');
				}
			});
}

function verify(id) {
	$('#win').dialog({
				title : '产品验证',
				width : 800,
				height : 540,
				href : '${contextPath!}/projectOnlineApply/verify?id=' + id,
				modal : true,
				buttons : [{
							text : '完成',
							handler : function() {
								var data = $("#editForm").serializeArray();
								$('#editForm').form('submit', {
											url : '${contextPath!}/projectOnlineApply/verify',
											queryParams : {
												result : 1
											},
											success : function(data) {
												var obj = $.parseJSON(data);
												if (obj.code == 200) {
													updateGrid(obj.data);
													$('#win').dialog('close');
												} else {
													$.messager.alert('错误', obj.result);
												}
											}
										});
							}
						}, {
							text : '失败',
							handler : function() {
								var data = $("#editForm").serializeArray();
								$('#editForm').form('submit', {
											url : '${contextPath!}/projectOnlineApply/verify',
											queryParams : {
												result : 0
											},
											success : function(data) {
												var obj = $.parseJSON(data);
												if (obj.code == 200) {
													updateGrid(obj.data)
													$('#win').dialog('close');
												} else {
													$.messager.alert('错误', obj.result);
												}
											}
										});
							}
						}]
			});

}

function appendSubsystem() {
	paramCount++;
	var content = '<tr class="subsystem">' + '<td colspan="2" class="table-title">系统名称</td>' + '<td colspan="2"><input class="easyui-combobox" name="subProjectName[' + paramCount
			+ ']" style="width: 97%; text-align: center;"' + 'data-options="url:\'${contextPath!}/project/list.json\',textField:\'name\',valueField:\'id\',required:true" /></td>'
			+ '<td class="table-title">负责人</td>' + '<td><select class="easyui-combobox" name="managerId[' + paramCount + ']"'
			+ 'data-options="url:\'${contextPath!}/member/members\',textField:\'realName\',valueField:\'id\',required:true"' + 'style="width: 60%; text-align: center;">' + '</select></td>' + '</tr>';
	$('.table-box .subsystem:last').after(content);
	$.parser.parse($('.table-box tr:eq(' + (paramCount + 9) + ')'));

}

function removeSubsystem() {
	if (paramCount <= 0) {
		return;
	}
	$('.table-box tr:eq(' + (paramCount + 9) + ')').remove();
	paramCount--;
}

function loadProject() {
	var id = $('#projectId').combobox('getValue');
	if (id) {
		$.post('${contextPath!}/project/listViewData.json', {
					id : id
				}, function(res) {
					if (res && res.length > 0) {
						var p = res[0];
						$('#projectManager').textbox('initValue', p.projectManager);
						$('#serialNumber').textbox('initValue', p.serialNumber);
						$('#productManager').textbox('initValue', p.productManager);
						$('#businessOwner').textbox('initValue', p.businessOwner);
						$('#testManager').textbox('initValue', p.testManager);
						$('#developManager').textbox('initValue', p.developManager);
						loadVersion(p.id);
					}
				}, 'json');
	}
}

function selectFirst() {
	if ($('#versionId').combobox('getData').length <= 0) {
		$('#versionId').combobox('setText', '');
		return;
	}
	$('#versionId').combobox('initValue', $('#versionId').combobox('getData')[0].id);
	$('#versionId').combobox('setText', $('#versionId').combobox('getData')[0].version);
}

function loadVersion(nval, oval) {
	$('#versionId').combobox('reload', '${contextPath!}/project/version/list?projectId=' + nval);
	$('#versionId').combobox('enable');
}

// 打开新增窗口
function openInsert() {
	$('#win').dialog({
				title : '上线申请',
				width : 800,
				height : 540,
				href : '${contextPath!}/projectOnlineApply/add',
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								var data = $("#editForm").serializeArray();
								$('#editForm').form('submit', {
											url : '${contextPath!}/projectOnlineApply/add',
											onSubmit : function() {
												if (!validateForm()) {
													return false;
												}
												showProgressbar();
											},
											success : function(data) {
												var obj = $.parseJSON(data);
												if (obj.code == 200) {
													updateGrid(obj.data);
													$('#win').dialog('close');
												} else {
													$.messager.alert('错误', obj.result);
												}
												closeProgressbar();
											}
										});
							}
						}, {
							text : '取消',
							handler : function() {
								$('#win').dialog('close');
							}
						}, {
							text : '提交',
							handler : function() {
								var data = $("#editForm").serializeArray();
								$('#editForm').form('submit', {
											url : '${contextPath!}/projectOnlineApply/saveAndSubmit',
											onSubmit : function() {
												return validateForm();
											},
											success : function(data) {
												var obj = $.parseJSON(data);
												if (obj.code == 200) {
													updateGrid(obj.data);
													$('#win').dialog('close');
												} else {
													$.messager.alert('错误', obj.result);
												}
											}
										});
							}
						}]
			});
}

function validateForm() {
	if (!$('#editForm').form('validate')) {
		return false;
	}
	if (!$('input[name=sqlScript]').val() && !$('input[name=sqlFile]').val()) {
		$.messager.alert('错误', 'sql脚本不能为空');
		return false;
	}
	if (!$('input[name=startupScript]').val() && !$('input[name=startupScriptFile]').val()) {
		$.messager.alert('错误', '启动脚本不能为空');
		return false;
	}
	if (!$('input[name=dependencySystem]').val() && !$('input[name=dependencySystemFile]').val()) {
		$.messager.alert('错误', '依赖系统不能为空');
		return false;
	}
	return true;
}

function updateGrid(row) {
	var index = $('#grid').datagrid('reload');
}

// 打开修改窗口
function openUpdate(index, id, taskId) {
	var selected = null;
	if (index >= 0) {
		selected = $('#grid').datagrid('getRows')[index]
	} else {
		selected = $('#grid').datagrid('getSelected');
	}
	if (!selected) {
		$.messager.alert('提示', '请选择一条记录');
		return;
	}
	if (selected.$_applyState != 1) {
		return;
	}
	if (selected.$_applicantId != userId) {
		return;
	}
	var url = '${contextPath!}/projectOnlineApply/update?id=' + selected.id + '&dialog=true';
	if (taskId) {
		url += '&taskId=' + taskId;
	}
	$('#win').dialog({
				title : '上线申请',
				width : 800,
				height : 540,
				href : url,
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								var data = $("#editForm").serializeArray();
								$('#editForm').form('submit', {
											url : '${contextPath!}/projectOnlineApply/update',
											onSubmit : function() {
												if (!validateForm()) {
													return false;
												}
												showProgressbar();
											},
											success : function(data) {
												var obj = $.parseJSON(data);
												if (obj.code == 200) {
													updateGrid(obj.data);
													$('#win').dialog('close');
												} else {
													$.messager.alert('错误', obj.result);
												}
												closeProgressbar();
											}
										});
							}
						}, {
							text : '取消',
							handler : function() {
								$('#win').dialog('close');
							}
						}, {
							text : '提交',
							handler : function() {
								var data = $("#editForm").serializeArray();
								$('#editForm').form('submit', {
											url : '${contextPath!}/projectOnlineApply/saveAndSubmit',
											onSubmit : function() {
												if (!validateForm()) {
													return false;
												}
												showProgressbar();
											},
											success : function(data) {
												var obj = $.parseJSON(data);
												if (obj.code == 200) {
													updateGrid(obj.data);
													$('#win').dialog('close');
												} else {
													$.messager.alert('错误', obj.result);
												}
												closeProgressbar();
											}
										});
							}
						}]
			});
}

var intervalFlag = undefined;

function closeProgressbar() {
	window.clearInterval(intervalFlag);
	var bar = $.messager.progress('bar');
	bar.progressbar('setValue', 100);
	bar.progressbar('setText', '100%');
	$.messager.progress('close');
}

function showProgressbar() {
	$.messager.progress({
				title : "提示",
				msg : "数据处理中......",
				interval : 0
			});
	var bar = $.messager.progress('bar');
	intervalFlag = window.setInterval(function() {
				var val = bar.progressbar('getValue');
				if (val >= 90) {
					window.clearInterval();
				}
				bar.progressbar('setValue', val + 10);
			}, 1000);
}

function saveOrUpdate() {
	if (!$('#_form').form("validate")) {
		return;
	}
	var _formData = removeKeyStartWith($("#_form").serializeObject(), "_");
	var _url = null;
	// 没有id就新增
	if (_formData.id == null || _formData.id == "") {
		_url = "${contextPath}/projectOnlineApply/insert";
	} else {// 有id就修改
		_url = "${contextPath}/projectOnlineApply/update";
	}
	$.ajax({
				type : "POST",
				url : _url,
				data : _formData,
				processData : true,
				dataType : "json",
				async : true,
				success : function(data) {
					if (data.code == "200") {
						$("#grid").datagrid("reload");
						$('#dlg').dialog('close');
					} else {
						$.messager.alert('错误', data.result);
					}
				},
				error : function() {
					$.messager.alert('错误', '远程访问失败');
				}
			});
}

// 根据主键删除
function del(id, index) {
	if (index != undefined) {
		$('#grid').datagrid('selectRow', index);
	}
	var selected = $("#grid").datagrid("getSelected");
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	$.messager.confirm('确认', '您确认想要删除记录吗？', function(r) {
				if (r) {
					$.ajax({
								type : "POST",
								url : "${contextPath}/projectOnlineApply/delete",
								data : {
									id : selected.id
								},
								processData : true,
								dataType : "json",
								async : true,
								success : function(data) {
									if (data.code == "200") {
										$("#grid").datagrid("deleteRow", index ? index : $('#grid').datagrid('getRowIndex', selected));
										$('#dlg').dialog('close');
									} else {
										$.messager.alert('错误', data.result);
									}
								},
								error : function() {
									$.messager.alert('错误', '远程访问失败');
								}
							});
				}
			});

}
// 表格查询
function queryGrid() {
	var opts = $("#grid").datagrid("options");
	if (null == opts.url || "" == opts.url) {
		opts.url = "${contextPath}/projectOnlineApply/listPage";
	}
	if (!$('#form').form("validate")) {
		return;
	}
	var param = bindMetadata("grid", true);
	var formData = $("#form").serializeObject();
	$.extend(param, formData);
	$("#grid").datagrid("load", param);
}

// 清空表单
function clearForm() {
	$('#form').form('clear');
	$('#applicantId').textbox('initValue', '');
}

// 表格表头右键菜单
function headerContextMenu(e, field) {
	e.preventDefault();
	if (!cmenu) {
		createColumnMenu("grid");
	}
	cmenu.menu('show', {
				left : e.pageX,
				top : e.pageY
			});
}

$.messager.progress({
			title : "提示",
			msg : "加载中,请稍候...",
			value : '10',
			text : '{value}%',
			interval : 200
		});
$.parser.onComplete = function() {
	$.messager.progress("close");
}
// 全局按键事件
function getKey(e) {
	e = e || window.event;
	var keycode = e.which ? e.which : e.keyCode;
	if (keycode == 46) { // 如果按下删除键
		var selected = $("#grid").datagrid("getSelected");
		if (selected && selected != null) {
			del();
		}
	}
}

/**
 * 绑定页面回车事件，以及初始化页面时的光标定位
 * 
 * @formId 表单ID
 * @elementName 光标定位在指点表单元素的name属性的值
 * @submitFun 表单提交需执行的任务
 */
$(function() {
			$('#applicantId').textbox('addClearBtn', 'icon-clear');
			if (document.addEventListener) {
				document.addEventListener("keyup", getKey, false);
			} else if (document.attachEvent) {
				document.attachEvent("onkeyup", getKey);
			} else {
				document.onkeyup = getKey;
			}
			queryGrid();
		});