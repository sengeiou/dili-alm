// 编辑行索引
var editIndex = undefined;

function rolesFormatter(value, row, index) {
	var content = '';
	if (!row.userInfo || !row.userInfo.roles) {
		return '';
	}
	$(row.userInfo.roles).each(function(index, item) {
				content += item.roleName + '，';
			});
	return content.substring(0, content.length - 1);
}

function isEditing() {
	return undefined != editIndex;
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
					if (!selected.deletable) {
						$.messager.alert('警告', '该数据不能删除');
					}
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
				field : 'memberId'
			});
	editor.target.textbox('setValue', row.$_memberId);
	editor.target.textbox('setText', row.memberId);
	editor = teamGrid.datagrid('getEditor', {
				index : index,
				field : 'role'
			});
	editor.target.textbox('setValue', row.$_role);
	editor.target.textbox('setText', row.role);
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
							field : 'adminRoles',
							width : '40%'
						}]);
	} else {
		teamGrid.datagrid('resizeColumn', [{
							field : 'adminRoles',
							width : '30%'
						}]);
	}
}

function hideCMAndShowOpt(id) {
	showOptButtons(id);
	teamGrid.datagrid('showColumn', 'opt');
}

function showCMAndHideOpt(id) {
	hideOptButtons(id);
	teamGrid.datagrid('hideColumn', 'opt');
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
function insertOrUpdateTeam(index, row, changes) {
	var postData = getOriginalData(row);
	var url = '${contextPath!}/team/';
	if (!row.id) {
		url += 'insert';
		postData.projectId = $('#projectId').val();
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
					row = data.data;
					$('#btnSaveTemp').prop('id', 'btnSave' + row.id);
					$('#btnCancelTemp').prop('id', 'btnCancel' + row.id);
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

// 清空表单
function clearForm() {
	$('#form').form('clear');
	$('#memberId').textbox('initValue', '');
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
	console.log(param);
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
				field : 'memberId'
			});
	row.memberId = editor.target.textbox('getText');
	row.$_memberId = editor.target.textbox('getValue');
	editor = $(this).datagrid('getEditor', {
				index : index,
				field : 'role'
			});
	row.role = editor.target.textbox('getText');
	row.$_role = editor.target.textbox('getValue');

	var isValid = teamGrid.datagrid('validateRow', index);
	if (!isValid) {
		return false;
	}
}

function onAfterEdit(index, row, changes) {
	resizeColumn(true);
	showCMAndHideOpt(index);
	insertOrUpdateTeam(index, row, changes);
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