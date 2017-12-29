var positionEditIndex = undefined;

function onDeptGridBeginEdit(index, row) {
	oldRecord = new Object();
	$.extend(true, oldRecord, row);
	var editor = $('#roleGrid').datagrid('getEditor', {
				index : index,
				field : 'roleId'
			});
	editor.target.textbox('initValue', row.$_roleId);
	editor.target.textbox('setText', row.memberId);
}

function onDeptGridEndEdit(index, row, changes) {
	var editor = $(this).datagrid('getEditor', {
				index : index,
				field : 'roleId'
			});
	row.roleId = editor.target.textbox('getText');
	row.$_roleId = editor.target.textbox('getValue');

	var isValid = $(this).datagrid('validateRow', index);
	if (!isValid) {
		return false;
	}
}

function onGridLoadSuccess() {
	$(this).datagrid('keyCtr');
}

function openInsertPosition() {
	if (!endEditing()) {
		$.messager.alert('警告', '有数据正在编辑');
		return;
	}
	positionEditIndex = $('#roleGrid').datagrid('getRows').length;
	$('#roleGrid').datagrid('appendRow', {});
	$('#roleGrid').datagrid('selectRow', positionEditIndex);
	resizePositionGridColumn();
	showPositionOptColumnAndButtons(positionEditIndex);
	$('#roleGrid').datagrid('beginEdit', positionEditIndex);
}

function positionColumnFormatter(value, row, index) {
	var content = '<input type="button" id="btnSave' + index + '" style="margin:0px 4px;display:none;" value="保存" onclick="javascript:savePosition();">';
	content += '<input type="button" id="btnCancel' + index + '" style="margin:0px 4px;display:none;" value="取消" onclick="javascript:cancelPositionEdit();">';
	return content;
}

function savePosition() {
	var grid = $('#roleGrid');
	if (grid.datagrid('validateRow', positionEditIndex)) {
		var isValid = grid.datagrid('validateRow', positionEditIndex);
		if (!isValid) {
			return false;
		}
		resizePositionGridColumn(true);
		hidePositionOptColumnAndButtons(positionEditIndex);
		grid.datagrid('endEdit', positionEditIndex);
		var selected = grid.datagrid('getSelected');
		if (!selected.id) {
			insertPosition(positionEditIndex);
		} else {
			updatePosition(positionEditIndex);
		}
		positionEditIndex = undefined;
	}
}

function openUpdatePosition() {
	var grid = $('#roleGrid');
	var selected = grid.datagrid('getSelected');
	if (!selected) {
		$.messager.alert('提示', '请选择一条数据');
		return;
	}
	positionEditIndex = grid.datagrid('getRowIndex', selected);
	resizePositionGridColumn();
	showPositionOptColumnAndButtons(positionEditIndex);
	grid.datagrid('beginEdit', positionEditIndex);
}

function cancelPositionEdit() {
	$('#roleGrid').datagrid('cancelEdit');
	$('#roleGrid').datagrid('rejectChanges');
	resizePositionGridColumn(true);
	hidePositionOptColumnAndButtons(positionEditIndex);
	positionEditIndex = undefined;
}

function showPositionOptColumnAndButtons(index) {
	showPositionOptButtons(index);
	$('#roleGrid').datagrid('showColumn', 'opt');
}

function hidePositionOptColumnAndButtons(index) {
	hidePositionOptButtons(index);
	$('#roleGrid').datagrid('hideColumn', 'opt');
}

/**
 * 显示编辑行最后一列的操作按钮
 * 
 * @param {}
 *            index 行索引
 */
function showPositionOptButtons(index) {
	$('#btnSave' + index + ',#btnCancel' + index).show();
}

/**
 * 显示编辑行最后一列的操作按钮
 * 
 * @param {}
 *            index 行索引
 */
function hidePositionOptButtons(index) {
	$('#BtnSave' + index + ',#btnCancel' + index).hide();
}

function updatePosition(positionEditIndex) {
	var url = '${contextPath!}/departmentRole/update';
	var grid = $('#roleGrid');
	var postData = getOriginalData(grid.datagrid('getChanges')[0]);
	postData.departmentId = $('#departmentId').val();
	$.ajax({
				url : url,
				data : postData,
				error : function(xhr, msg, ex) {
					grid.datagrid('rejectChanges');
					$.messager.alert('提示', '请求失败！');
				},
				success : function(data) {
					if (data.code != 200) {
						grid.datagrid('rejectChanges');
						$.messager.alert('提示', data.result);
						return;
					} else {
						grid.datagrid('updateRow', {
									index : positionEditIndex,
									row : data.data
								});
						grid.datagrid('acceptChanges');
					}
				}
			});
}

function insertPosition(index) {
	var url = '${contextPath!}/departmentRole/insert';
	var grid = $('#roleGrid');
	var postData = getOriginalData(grid.datagrid('getChanges', 'inserted')[0]);
	postData.departmentId = $('#departmentId').val();
	$.ajax({
				url : url,
				data : postData,
				error : function(xhr, msg, ex) {
					grid.datagrid('rejectChanges');
					$.messager.alert('提示', '请求失败！');
				},
				success : function(data) {
					if (data.code != 200) {
						grid.datagrid('rejectChanges');
						$.messager.alert('提示', data.result);
						return;
					} else {
						grid.datagrid('updateRow', {
									index : index,
									row : data.data
								});
						grid.datagrid('acceptChanges');
					}
				}
			});
}

function deletePosition() {
	var grid = $('#roleGrid');
	var selected = grid.datagrid('getSelected');
	if (!selected) {
		$.messager.alert('提示', '请选择一条数据');
		return;
	}
	$.messager.confirm('提示', '确定要删除该条数据？', function(flag) {
				if (!flag) {
					return false;
				}
				$.ajax({
							url : '${contextPath!}/departmentRole/delete',
							data : {
								id : selected.id
							},
							error : function(xhr, msg, ex) {
								grid.datagrid('rejectChanges');
								$.messager.alert('提示', '请求失败！');
							},
							success : function(data) {
								if (data.code != 200) {
									$.messager.alert('提示', data.result);
									return;
								} else {
									var index = grid.datagrid('getRowIndex', selected);
									grid.datagrid('deleteRow', index);
									grid.datagrid('acceptChanges');
								}
							}
						});
			});
}

function resizePositionGridColumn(original) {
	if (original) {
		$('#roleGrid').datagrid('resizeColumn', [{
							field : 'roleId',
							width : '80%'
						}]);
	} else {
		$('#roleGrid').datagrid('resizeColumn', [{
							field : 'roleId',
							width : '60%'
						}]);
	}
}

function endPositionEditing() {
	var grid = $('#roleGrid');
	if (positionEditIndex == undefined) {
		return true
	}
	if (grid.datagrid('validateRow', positionEditIndex)) {
		grid.datagrid('endEdit', positionEditIndex);
		positionEditIndex = undefined;
		return true;
	} else {
		return false;
	}
}

function cancelEdit() {
	var grid = $('#roleGrid');
	if (positionEditIndex == undefined) {
		return;
	}
	grid.datagrid('cancelEdit', positionEditIndex);
	positionEditIndex = undefined;
};

function onCancelEdit(index, row) {
	var grid = $('#roleGrid');
	if (!row.id) {
		grid.datagrid('deleteRow', index);
	}
	hideOptButtons(this.id, index);
	if (gridType == 1 && this.id == 'grid') {
		grid.datagrid('resizeColumn', [{
							field : 'description',
							width : '60%'
						}]);
	} else {
		grid.datagrid('resizeColumn', [{
							field : 'menuUrl',
							width : '35%'
						}, {
							field : 'description',
							width : '40%'
						}]);
	}
	grid.datagrid('hideColumn', 'opt');
}

$.extend($.fn.datagrid.methods, {
			keyCtr : function(jq) {
				return jq.each(function() {
							var grid = $(this);
							var gridId = this.id;
							grid.datagrid('getPanel').panel('panel').attr('tabindex', 1).bind('keydown', function(e) {
										switch (e.keyCode) {
											case 46 :
												if (positionEditIndex) {
													return;
												}
												var selected = grid.datagrid("getSelected");
												if (selected && selected != null) {
													deletePosition();
												}
												break;
											case 13 :
												endPositionEditing();
												break;
											case 27 :
												cancelPositionEdit();
												break;
											case 38 :
												if (!endPositionEditing()) {
													return;
												}
												var selected = grid.datagrid("getSelected");
												if (!selected) {
													return;
												}
												var selectedIndex = grid.datagrid('getRowIndex', selected);
												if (selectedIndex <= 0) {
													return;
												}
												endEditing(gridId);
												grid.datagrid('selectRow', --selectedIndex);
												break;
											case 40 :
												if (!endPositionEditing()) {
													return;
												}
												if (grid.datagrid('getRows').length <= 0) {
													openInsertPosition();
													return;
												}
												var selected = grid.datagrid("getSelected");
												if (!selected) {
													grid.datagrid('selectRow', 0);
													return;
												}
												var selectedIndex = grid.datagrid('getRowIndex', selected);
												if (selectedIndex == grid.datagrid('getRows').length - 1) {
													openInsertPosition();
												} else {
													grid.datagrid('selectRow', ++selectedIndex);
												}
												break;
										}
									});
						});
			}
		});