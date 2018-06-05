var userId = '${user.id!}';
userId = parseInt(userId);

function serialNumberFormatter(value, row, index) {
	return '<a href="javascript:void(0);" onclick="detail(' + row.id + ');">' + value + '</a>';
}

function detail(id) {
	$('#win').dialog({
				title : '工单详情',
				width : 800,
				height : 540,
				href : '${contextPath!}/workOrder/detail?id=' + id,
				modal : true,
				buttons : [{
							text : '返回',
							handler : function() {
								$('#win').dialog('close');
							}
						}]
			});
}

function removeTreeAdminUser(node, data) {
	var me = $(this);
	$(data).each(function(index, item) {
				if (item.id == 1) {
					var node = me.tree('find', item.id);
					me.tree('remove', node.target);
					return false;
				}
			});
}

function removeAdminUserFilter(data) {
	$(data).each(function(index, item) {
				if (item.id == 1) {
					data.splice(index, 1);
					return false;
				}
			});
	return data;
}

function operationFormatter(value, row, index) {
	var content = '';
	if (row.$_workOrderState == 1) {
		if (row.$_applicantId == userId) {
			content += '<a style="padding:0px 2px;" href="javascript:void(0);" onclick="openUpdate(' + row.id + ',' + index + ');">编辑</a>';
			content += '<a style="padding:0px 2px;" href="javascript:void(0);" onclick="del(' + row.id + ');">删除</a>';
		} else {
			content += '<span style="padding:0px 2px;">编辑</a>';
			content += '<span style="padding:0px 2px;">删除</a>';
		}
	} else if (row.$_workOrderState == 2) {
		if (row.$_acceptorId == userId) {
			content += '<a style="padding:0px 2px;" href="javascript:void(0);" onclick="allocate(' + row.id + ');">分配</a>';
		} else {
			content += '<span style="padding:0px 2px;">分配</a>';
		}
	} else if (row.$_workOrderState == 3) {
		if (row.$_executorId == userId) {
			content += '<a style="padding:0px 2px;" href="javascript:void(0);" onclick="solve(' + row.id + ');">完成</a>';
		} else {
			content += '<span style="padding:0px 2px;">完成</a>';
		}
	} else if (row.$_workOrderState == 4) {
		if (row.$_executorId == userId) {
			content += '<a style="padding:0px 2px;" href="javascript:void(0);" onclick="closeWorkOrder(' + row.id + ');">关闭</a>';
		} else {
			content += '<span style="padding:0px 2px;">关闭</a>';
		}
	}
	return content;
}

function closeWorkOrder(id) {
	$.messager.confirm('提示', '工单关闭表示你确认该工单已经关闭，确认关闭吗？', function(f) {
				if (!f) {
					return false;
				}
				$.post('${contextPath!}/workOrder/close', {
							id : id
						}, function(res) {
							if (res.success) {
								updateGridRow(res.data);
							} else {
								$.messager.alert('提示', res.result);
							}
						});
			});
}

function validateForm() {
	if (!$('#editForm').form('validate')) {
		return false;
	}
	var value = $('#acceptorId').combobox('getValue');
	if (isNaN(value)) {
		$.messager.alert('提示', '受理人不存在，请重新选择！', function() {
					$('#acceptorId').combobox('initValue', '');
					$('#acceptorId').combobox('setText', '');
				});
		return false;
	}
	var value = $('#copyUserIds').combotree('tree').tree('getSelected');
	if (!value) {
		$.messager.alert('提示', '请选择抄送人！', null, function() {
					$('#copyUserIds').combotree('clear');
				});
		return false;
	}
}

function updateGridRow(row) {
	var index = $('#grid').datagrid('getRowIndex', $('#grid').datagrid('getSelected'));
	if (index >= 0) {
		$('#grid').datagrid('updateRow', {
					index : index,
					row : row
				});
	} else {
		$('#grid').datagrid('appendRow', row);
	}
	$('#grid').datagrid('acceptChanges');
}

// 打开新增窗口
function openInsert() {
	$('#win').dialog({
				title : '新增工单',
				width : 800,
				height : 540,
				href : '${contextPath!}/workOrder/add',
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								var data = $("#editForm").serializeArray();
								$('#editForm').form('submit', {
											url : '${contextPath!}/workOrder/saveOrUpdate',
											onSubmit : function() {
												return validateForm();
											},
											success : function(data) {
												var obj = $.parseJSON(data);
												if (obj.code == 200) {
													$('#grid').datagrid('appendRow', obj.data);
													$('#grid').datagrid('acceptChanges');
													$('#win').dialog('close');
												} else {
													$.messager.alert('错误', obj.result);
												}
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
											url : '${contextPath!}/workOrder/saveAndSubmit',
											onSubmit : function() {
												return validateForm();
											},
											success : function(data) {
												var obj = $.parseJSON(data);
												if (obj.code == 200) {
													updateGridRow(obj.data);
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

// 打开修改窗口
function openUpdate(id, index) {
	var selected = null;
	if (!id) {
		selected = $("#grid").datagrid("getSelected");
		if (!selected) {
			$.messager.alert('警告', '请选中一条数据');
			return;
		}
		id = selected.id;
		index = $("#grid").datagrid("getRowIndex", selected);
	}
	if (selected.$_workOrderState != 1) {
		return false;
	}
	$('#win').dialog({
				title : '工单编辑',
				width : 800,
				height : 540,
				href : '${contextPath!}/workOrder/update?id=' + id,
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								var data = $("#editForm").serializeArray();
								$('#editForm').form('submit', {
											url : '${contextPath!}/workOrder/saveAndSubmit',
											onSubmit : function() {
												validateForm();
											},
											success : function(data) {
												var obj = $.parseJSON(data);
												if (obj.code == 200) {
													updateGridRow(obj.data);
													$('#win').dialog('close');
												} else {
													$.messager.alert('错误', obj.result);
												}
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
											url : '${contextPath!}/workOrder/saveAndSubmit',
											onSubmit : function() {
												if (!$(this).form('validate')) {
													return false;
												}
											},
											success : function(data) {
												var obj = $.parseJSON(data);
												if (obj.code == 200) {
													updateGridRow(obj.data);
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

function allocatePost(id, result) {
	var data = $("#editForm").serializeArray();
	$('#editForm').form('submit', {
				url : '${contextPath!}/workOrder/allocate',
				queryParams : {
					result : result
				},
				onSubmit : function() {
					if (result == 1 && !$(this).form('validate')) {
						return false;
					}
					var executorId = $('#executorId').combobox('getValue');
					if (isNaN(executorId)) {
						$.messager.alert('提示', '执行人不存在，请重新选择！', null, function() {
									$('#executorId').combobox('clear');
								});
					}
				},
				success : function(data) {
					var obj = $.parseJSON(data);
					if (obj.code == 200) {
						updateGridRow(obj.data);
						$('#win').dialog('close');
					} else {
						$.messager.alert('错误', obj.result);
					}
				}
			});
}

function allocate(id) {
	$('#win').dialog({
				title : '工单分配',
				width : 800,
				height : 540,
				href : '${contextPath!}/workOrder/allocate?id=' + id,
				modal : true,
				buttons : [{
							text : '确认',
							handler : function() {
								allocatePost(id, 1);
							}
						}, {
							text : '回退',
							handler : function() {
								allocatePost(id, 0);
							}
						}]
			});
}

function solve(id) {
	$('#win').dialog({
				title : '工单解决',
				width : 800,
				height : 540,
				href : '${contextPath!}/workOrder/solve?id=' + id,
				modal : true,
				buttons : [{
							text : '确认',
							handler : function() {
								var data = $("#editForm").serializeArray();
								$('#editForm').form('submit', {
											url : '${contextPath!}/workOrder/solve',
											onSubmit : function() {
												if (!$(this).form('validate')) {
													return false;
												}
											},
											success : function(data) {
												var obj = $.parseJSON(data);
												if (obj.code == 200) {
													updateGridRow(obj.data);
													$('#win').dialog('close');
												} else {
													$.messager.alert('错误', obj.result);
												}
											}
										});
							}
						}, {
							text : '返回',
							handler : function() {
								$('#win').dialog('close');
							}
						}]
			});
}

// 根据主键删除
function del() {
	var selected = $("#grid").datagrid("getSelected");
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	$.messager.confirm('确认', '您确认想要删除记录吗？', function(r) {
				if (r) {
					$.ajax({
								type : "POST",
								url : "${contextPath}/workOrder/delete",
								data : {
									id : selected.id
								},
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
			});

}
// 表格查询
function queryGrid() {
	var opts = $("#grid").datagrid("options");
	if (null == opts.url || "" == opts.url) {
		opts.url = "${contextPath}/workOrder/listPage";
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
	$('#applicantId').textbox('initValue', '');
	$('#applicantId').textbox('setText', '');
	$('#acceptorId1').textbox('initValue', '');
	$('#acceptorId1').textbox('setText', '');
	$('#form').form('clear');
	queryGrid();
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
			$('#acceptorId1').textbox('addClearBtn', 'icon-clear');
			queryGrid();
		})