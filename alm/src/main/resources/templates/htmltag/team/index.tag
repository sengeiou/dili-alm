// 编辑行索引
var editIndex = undefined;

function isEditing() {
	return undefined != editIndex;
}

// 清空表单
function clearForm() {
	$('#form').form('clear');
}

// 结束行编辑
function endEditing() {
	if (editIndex == undefined) {
		return true
	}
	if (teamGrid.datagrid('validateRow', editIndex)) {
		teamGrid.datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	} else {
		return false;
	}
}

// 新增一行空数据并开启编辑模式
function openInsert() {
	if (!dataAuth.addTeam) {
		return;
	}
	if (!endEditing()) {
		$.messager.alert('警告', '有数据正在编辑');
		return;
	}
	editIndex = 0;
	teamGrid.datagrid('insertRow', {
				index : editIndex,
				row : {
					memberState : '加入',
					$_memberState : 1
				}
			});
	teamGrid.datagrid('selectRow', editIndex);
	teamGrid.datagrid('beginEdit', editIndex);
}

// 开启选中行的编辑模式
function openUpdate() {
	if (!dataAuth.updateTeam) {
		return;
	}
	var selected = teamGrid.datagrid("getSelected");
	if (!selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	var index = teamGrid.datagrid('getRowIndex', selected);
	if (endEditing()) {
		teamGrid.datagrid('selectRow', index).datagrid('beginEdit', index);
		editIndex = index;
	}
}

// 根据主键删除
function del() {
	if (!dataAuth.deleteTeam) {
		return;
	}
	var selected = teamGrid.datagrid("getSelected");
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	$.messager.confirm('确认', '您确认想要删除记录吗？', function(r) {
				if (r) {
					$.ajax({
								type : "POST",
								url : '${contextPath!}/team/delete',
								data : {
									id : selected.id
								},
								processData : true,
								dataType : "json",
								async : true,
								success : function(data) {
									if (data.code == "200") {
										teamGrid.datagrid('deleteRow', teamGrid.datagrid('getRowIndex', selected));
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

var columnFormatter = function(value, row, index) {
	var id = row.id && row.id > 0 ? row.id : 'Temp';
	var content = '<input type="button" id="btnSave' + id + '" style="margin:0px 4px;display:none;" value="保存" onclick="javascript:endEditing();">';
	content += '<input type="button" id="btnCancel' + id + '" style="margin:0px 4px;display:none;" value="取消" onclick="javascript:cancelEdit();">';
	return content;
};

// 全局按键事件
function getKey(e) {
	e = e || window.event;
	var keycode = e.which ? e.which : e.keyCode;
	switch (keycode) {
		case 46 :
			if (isEditing())
				return;
			var selected = teamGrid.datagrid("getSelected");
			if (selected && selected != null) {
				del();
			}
			break;
		case 13 :
			if ($("#smDialog").parent().is(":hidden")) {
				endEditing();
			}
			break;
		case 27 :
			cancelEdit();
			break;
		case 38 :
			if (!endEditing()) {
				return;
			}
			var selected = teamGrid.datagrid("getSelected");
			if (!selected) {
				return;
			}
			var selectedIndex = teamGrid.datagrid('getRowIndex', selected);
			if (selectedIndex <= 0) {
				return;
			}
			endEditing();
			teamGrid.datagrid('selectRow', --selectedIndex);
			break;
		case 40 :
			if (!endEditing()) {
				return;
			}
			if (teamGrid.datagrid('getRows').length <= 0) {
				openInsert();
				return;
			}
			var selected = teamGrid.datagrid("getSelected");
			if (!selected) {
				teamGrid.datagrid('selectRow', 0);
				return;
			}
			var selectedIndex = teamGrid.datagrid('getRowIndex', selected);
			if (selectedIndex == teamGrid.datagrid('getRows').length - 1) {
				openInsert();
			} else {
				teamGrid.datagrid('selectRow', ++selectedIndex);
			}
			break;
	}
}

/**
 * 双击行的处理方法
 * 
 * @param {}
 *            index 行索引
 * @param {}
 *            field 行数据
 */
function onDblClickRow(index, field) {
	openUpdate();
}

/**
 * 显示编辑行最后一列的操作按钮
 * 
 * @param {}
 *            index 行索引
 */
function showOptButtons(index) {
	$('#btnSave' + index + ',#btnCancel' + index).show();
}

/**
 * 显示编辑行最后一列的操作按钮
 * 
 * @param {}
 *            index 行索引
 */
function hideOptButtons(index) {
	$('#btnSave' + index + ',#btnCancel' + index).hide();
}

var oldRecord = undefined;

/**
 * 开始编辑行的毁掉函数
 * 
 * @param {}
 *            index 行索引
 * @param {}
 *            row 行数据
 */
function onBeginEdit(index, row) {
	oldRecord = new Object();
	$.extend(true, oldRecord, row);
	var editor = teamGrid.datagrid('getEditor', {
				index : index,
				field : 'projectId'
			});
	editor.target.textbox('setValue', row.$_projectId);
	editor.target.textbox('setText', row.projectId);
	editor = teamGrid.datagrid('getEditor', {
				index : index,
				field : 'memberId'
			});
	editor.target.textbox('setValue', row.$_memberId);
	editor.target.textbox('setText', row.memberId);
	editor = teamGrid.datagrid('getEditor', {
				index : index,
				field : 'type'
			});
	editor.target.textbox('setValue', row.$_type);
	editor.target.textbox('setText', row.type);
	editor = teamGrid.datagrid('getEditor', {
				index : index,
				field : 'memberState'
			});
	editor.target.textbox('setValue', row.$_memberState);
	editor.target.textbox('setText', row.memberState);
	resizeColumn();
	hideCMAndShowOpt(row.id);
}

/**
 * 设置列宽，编辑表格的时候要显示操作列并隐藏不可编辑列
 * 
 * @param {}
 *            original
 */
function resizeColumn(original) {
	if (original) {
		teamGrid.datagrid('resizeColumn', [{
							field : 'projectId',
							width : '20%'
						}, {
							field : 'memberId',
							width : '20%'
						}, {
							field : 'type',
							width : '10%'
						}, {
							field : 'memberState',
							width : '10%'
						}]);
	} else {
		teamGrid.datagrid('resizeColumn', [{
							field : 'projectId',
							width : '25%'
						}, {
							field : 'memberId',
							width : '25%'
						}, {
							field : 'type',
							width : '15%'
						}, {
							field : 'memberState',
							width : '15%'
						}]);
	}
}

function hideCMAndShowOpt(id) {
	showOptButtons(id);
	teamGrid.datagrid('showColumn', 'opt');
	teamGrid.treegrid('hideColumn', 'joinTime');
	teamGrid.treegrid('hideColumn', 'leaveTime');
}

function showCMAndHideOpt(id) {
	hideOptButtons(id);
	teamGrid.datagrid('hideColumn', 'opt');
	teamGrid.treegrid('showColumn', 'joinTime');
	teamGrid.treegrid('showColumn', 'leaveTime');
}

function showOptButtons(id) {
	id = id && id > 0 ? id : 'Temp';
	$('#btnSave' + id + ',#btnCancel' + id).show();
}

/**
 * 显示编辑行最后一列的操作按钮
 * 
 * @param {}
 *            index 行索引
 */
function hideOptButtons(id) {
	id = id && id > 0 ? id : 'Temp';
	$('#btnSave' + id + ',#btnCancel' + id).hide();
}

/**
 * 插入或者修改菜单信息
 * 
 * @param {}
 *            node 菜单树被选中的节点
 * @param {}
 *            index 行索引
 * @param {}
 *            row 行数据
 * @param {}
 *            changes 被修改的数据
 */
function insertOrUpdateMenu(index, row, changes) {
	var postData = getOriginalData(row);
	var url = '${contextPath!}/team/';
	if (!row.id) {
		url += 'insert';
	} else {
		url += 'update'
	}
	$.post(url, postData, function(data) {
				if (data.code != 200) {
					if (oldRecord) {
						teamGrid.datagrid('updateRow', {
									index : index,
									row : oldRecord
								});
					} else {
						teamGrid.datagrid('deleteRow', index);
					}
					$.messager.alert('提示', data.result);
					return;
				}
				if (!row.id) {
					row.id = data.data.id;
					$('#btnSaveTemp').prop('id', 'btnSave' + row.id);
					$('#btnCancelTemp').prop('id', 'btnCancel' + row.id);
				}
				if (data.data.joinTime) {
					var date = new Date()
					date.setTime(data.data.joinTime);
					row.joinTime = date.Format('yyyy-MM-dd HH:mm:ss');
				}
				if (data.data.leaveTime) {
					var date = new Date()
					date.setTime(data.data.leaveTime);
					row.leaveTime = date.Format('yyyy-MM-dd HH:mm:ss');
				}
				teamGrid.datagrid('updateRow', {
							index : index,
							row : row
						});
				teamGrid.datagrid('refreshRow', index);
			}, 'json');
}

/**
 * 单击含回调方法，逻辑是结束之前的行编辑模式
 * 
 * @param {}
 *            index
 * @param {}
 *            row
 */
function onClickRow(index, row) {
	if (editIndex == index) {
		return;
	}
	if (endEditing()) {
		return;
	}
	teamGrid.datagrid('cancelEdit', editIndex);
	if (!row.id) {
		teamGrid.datagrid('deleteRow', editIndex);
		editIndex = undefined;
	}
}

/**
 * 取消行编辑
 */
function cancelEdit() {
	if (editIndex == undefined) {
		return;
	}
	teamGrid.datagrid('cancelEdit', editIndex);
	editIndex = undefined;
};

/**
 * 取消行编辑回调方法
 * 
 * @param {}
 *            index
 * @param {}
 *            row
 */
function onCancelEdit(index, row) {
	if (!row.id) {
		teamGrid.datagrid('deleteRow', index);
	}
	resizeColumn(true);
	showCMAndHideOpt(index);
}

function editorCallback(field) {
	var selected = teamGrid.datagrid("getSelected");
	var index = teamGrid.datagrid('getRowIndex', selected);
	var editor = teamGrid.datagrid('getEditor', {
				index : index,
				field : field
			});
	$(editor.target).attr("id", field + "_" + index);
	// $("#"+field+"_"+index).textbox("setValue","1234");
	// $("#"+field+"_"+index).textbox("setText","213231");
	showMembersDlg(field + "_" + index);
}
// 表格查询
function queryGrid() {
	var opts = $("#grid").datagrid("options");
	if (null == opts.url || "" == opts.url) {
		opts.url = "${contextPath}/team/listPage";
	}
	if (!$('#form').form("validate")) {
		return;
	}
	var param = bindMetadata("grid", true);
	var formData = $("#form").serializeObject();
	$.extend(param, formData);
	$("#grid").datagrid("load", param);
}

/**
 * 结束编辑回调方法
 * 
 * @param {}
 *            index
 * @param {}
 *            row
 */
function onEndEdit(index, row) {
	var editor = $(this).datagrid('getEditor', {
				index : index,
				field : 'projectId'
			});
	row.projectId = editor.target.combobox('getText');
	row.$_projectId = editor.target.combobox('getValue');
	editor = $(this).datagrid('getEditor', {
				index : index,
				field : 'memberId'
			});
	row.memberId = editor.target.textbox('getText');
	row.$_memberId = editor.target.textbox('getValue');
	editor = $(this).datagrid('getEditor', {
				index : index,
				field : 'type'
			});
	row.type = editor.target.combobox('getText');
	row.$_type = editor.target.combobox('getValue');
	editor = $(this).datagrid('getEditor', {
				index : index,
				field : 'memberState'
			});
	row.memberState = editor.target.textbox('getText');
	row.$_memberState = editor.target.textbox('getValue');
	var isValid = teamGrid.datagrid('validateRow', index);
	if (!isValid) {
		return false;
	}
}

function onAfterEdit(index, row, changes) {
	resizeColumn(true);
	showCMAndHideOpt(index);
	insertOrUpdateMenu(index, row, changes);
}

/**
 * 绑定页面回车事件，以及初始化页面时的光标定位
 * 
 * @formId 表单ID
 * @elementName 光标定位在指点表单元素的name属性的值
 * @submitFun 表单提交需执行的任务
 */
$(function() {
			$('#memberId').textbox('addClearBtn', 'icon-clear');
			window.teamGrid = $('#grid');
			if (document.addEventListener) {
				document.addEventListener("keyup", getKey, false);
			} else if (document.attachEvent) {
				document.attachEvent("onkeyup", getKey);
			} else {
				document.onkeyup = getKey;
			}

		});