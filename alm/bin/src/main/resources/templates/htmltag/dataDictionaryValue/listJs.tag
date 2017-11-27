var ddValueEditIndex = undefined;

// 结束行编辑
function endDdValueGridEditing() {
	if (ddValueEditIndex == undefined) {
		return true
	}
	if (ddValueGrid.datagrid('validateRow', ddValueEditIndex)) {
		ddValueGrid.datagrid('endEdit', ddValueEditIndex);
		ddValueEditIndex = undefined;
		return true;
	} else {
		return false;
	}
}

function cancelEditDdValue() {
	if (!ddValueGrid) {
		return;
	}

	ddValueGrid.datagrid('cancelEdit', ddValueEditIndex);
	ddValueEditIndex = undefined;
}

function formatOpsDdValue(value, row, index) {
	var content = '<input type="button" id="btnSaveDdValue' + index + '" style="margin:0px 4px;display:none;" value="保存" onclick="javascript:endDdValueGridEditing();">';
	content += '<input type="button" id="btnCancelDdValue' + index + '" style="margin:0px 4px;display:none;" value="取消" onclick="javascript:cancelEditDdValue();">';
	return content;
}

function onBeginEditDdValue(index, row) {
	showDdValueOptButtons(index);
	ddValueGrid.datagrid('resizeColumn', {
				field : 'notes',
				width : '20%'
			});

	ddValueGrid.datagrid('showColumn', 'opt');
	var editors = ddValueGrid.datagrid('getEditors', index);
	editors[0].target.trigger('focus');
}

function onAfterEditDdValue(index, row, changes) {
	var isValid = ddValueGrid.datagrid('validateRow', index);
	if (!isValid) {
		return false;
	}

	ddValueGrid.datagrid('resizeColumn', [{
						field : 'notes',
						width : '40%'
					}]);

	hideDdValueOptButtons(index);
	ddValueGrid.datagrid('hideColumn', 'opt');
	insertOrUpdateDdValue(index, row, changes);
}

function onCancelEditDdValue(index, row) {
	if (!row.id) {
		ddValueGrid.datagrid('deleteRow', index);
	}
	ddValueGrid.datagrid('resizeColumn', [{
						field : 'notes',
						width : '40%'
					}]);
	hideDdValueOptButtons(index);
	ddValueGrid.datagrid('hideColumn', 'opt');
	ddValueGrid.datagrid('removeEditor', 'code');
}

// 打开新增窗口
function openInsertDdValue() {
	if (!endDdValueGridEditing()) {
		return;
	}

	ddValueGrid.datagrid('addEditor', [{
						field : 'code',
						editor : {
							type : 'textbox',
							options : {
								required : true
							}
						}
					}]);
	ddValueEditIndex = ddValueGrid.datagrid('getRows').length;
	ddValueGrid.datagrid('appendRow', {
				type : 0
			});
	ddValueGrid.datagrid('selectRow', ddValueEditIndex);
	ddValueGrid.datagrid('beginEdit', ddValueEditIndex);
}

// 打开修改窗口
function openUpdateDdValue() {
	if (!endDdValueGridEditing()) {
		return;
	}

	var selected = ddValueGrid.datagrid("getSelected");
	if (!selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	var index = ddValueGrid.datagrid('getRowIndex', selected);
	if (endDdValueGridEditing()) {
		ddValueGrid.datagrid('selectRow', index).datagrid('beginEdit', index);
		ddValueEditIndex = index;
	}
}

function showDdValueOptButtons(index) {
	$('#btnSaveDdValue' + index + ',#btnCancelDdValue' + index).show();
}

function hideDdValueOptButtons(index) {
	$('#btnSaveDdValue' + index + ',#btnCancelDdValue' + index).hide();
}

function getSelectedTreeNode() {
	var selectedNode = ddValueTree.tree('getSelected');
	if (!selectedNode) {
		selectedNode = ddValueTree.tree('getRoot');
	}
	return selectedNode;
}

function insertOrUpdateDdValue(index, row, changes) {
	var oldRecord;
	var url = contextPath + '/dataDictionaryValue/';
	if (!row.id) {
		var selectedNode = getSelectedTreeNode();
		row.parentId = selectedNode.id > 0 ? selectedNode.id : undefined;
		row.ddId = ddId;
		url += 'insert';
	} else {
		oldRecord = new Object();
		$.extend(true, oldRecord, row);
		url += 'update'
	}
	$.post(url, row, function(data) {
				if (data.code != 200) {
					if (oldRecord) {
						ddValueGrid.datagrid('updateRow', {
									index : index,
									row : oldRecord
								});
					} else {
						ddValueGrid.datagrid('deleteRow', index);
					}
					$.messager.alert('提示', data.result);
					return;
				}
				if (!row.id) {
					row.id = data.data.id;
					ddValueTree.tree('append', {
								parent : getSelectedTreeNode().target,
								data : [{
											id : data.data.id,
											parentId : data.data.parentId,
											name : data.data.code,
											attributes : {
												ddId : data.data.ddId
											}
										}]
							});
				}
				ddValueGrid.datagrid('updateRow', {
							index : index,
							row : row
						});
				ddValueGrid.datagrid('refreshRow', index);
			}, 'json');
}

// 根据主键删除
function delDdValue() {
	if (!ddValueGrid) {
		return;
	}

	var selected = ddValueGrid.datagrid("getSelected");
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	$.messager.confirm('确认', '您确认想要删除记录吗？', function(r) {
				if (r) {
					$.ajax({
								type : "POST",
								url : contextPath + '/dataDictionaryValue/delete',
								data : {
									id : selected.id
								},
								processData : true,
								dataType : "json",
								async : true,
								success : function(data) {
									if (data.code == "200") {
										ddValueGrid.datagrid('deleteRow', ddValueGrid.datagrid('getRowIndex', selected));
										ddValueTree.tree('remove', ddValueTree.tree('find', selected.id).target);
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
function queryDdValueGrid(selectedNode) {
	var opts = $('#ddValueGrid').datagrid("options");
	if (null == opts.url || "" == opts.url) {
		opts.url = "${contextPath}/dataDictionaryValue/listPage";
	}
	var param = bindMetadata("ddValueGrid", true);
	param.ddId = window.ddId;
	var selectedNode = getSelectedTreeNode();
	param.parentId = selectedNode.id;
	$('#ddValueGrid').datagrid("load", param);
}
