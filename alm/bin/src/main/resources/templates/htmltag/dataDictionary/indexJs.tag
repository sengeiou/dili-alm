// 编辑行索引
var ddGridEditIndex = undefined;
var ddValueGrid = undefined;

// 结束行编辑
function endDdGridEditing() {
	if (ddGridEditIndex == undefined) {
		return true
	}
	if (ddGrid.datagrid('validateRow', ddGridEditIndex)) {
		ddGrid.datagrid('endEdit', ddGridEditIndex);
		ddGridEditIndex = undefined;
		return true;
	} else {
		return false;
	}
}

/**
 * 取消行编辑
 */
function cancelDdGridEdit() {
	ddGrid.datagrid('cancelEdit', ddGridEditIndex);
	ddGridEditIndex = undefined;
};

// 新增一行空数据并开启编辑模式
function openInsertDd() {
	ddGrid.datagrid('addEditor', [{
						field : 'code',
						editor : {
							type : 'textbox',
							options : {
								required : true
							}
						}
					}]);
	ddGridEditIndex = ddGrid.datagrid('getRows').length;
	ddGrid.datagrid('appendRow', {
				type : 0
			});
	ddGrid.datagrid('selectRow', ddGridEditIndex);
	ddGrid.datagrid('beginEdit', ddGridEditIndex);
}

// 开启选中行的编辑模式
function openUpdateDd() {
	if (!endDdGridEditing()) {
		return;
	}
	var selected = ddGrid.datagrid("getSelected");
	if (!selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	var index = ddGrid.datagrid('getRowIndex', selected);
	if (endDdGridEditing()) {
		ddGrid.datagrid('selectRow', index).datagrid('beginEdit', index);
		ddGridEditIndex = index;
	}
}

// 根据主键删除
function delDd(selected) {
	selected = null == selected ? ddGrid.datagrid("getSelected") : selected;
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	$.messager.confirm('确认', '您确认想要删除记录吗？', function(r) {
				if (r) {
					$.ajax({
								type : "POST",
								url : '${contextPath!}/dataDictionary/delete',
								data : {
									id : selected.id
								},
								processData : true,
								dataType : "json",
								async : true,
								success : function(data) {
									if (data.code == "200") {
										ddGrid.datagrid('deleteRow', ddGrid.datagrid('getRowIndex', selected));
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
function queryDdGrid() {
	var opts = ddGrid.datagrid("options");
	if (null == opts.url || "" == opts.url) {
		opts.url = "${contextPath}/dataDictionary/listPage";
	}
	if (!$('#form').form("validate")) {
		return;
	}
	var param = bindMetadata("grid", true);
	var formData = $("#form").serializeObject();
	$.extend(formData, param);
	ddGrid.datagrid("load", formData);
}

// 全局按键事件
function getKey(e) {
	e = e || window.event;
	var keycode = e.which ? e.which : e.keyCode;
	switch (keycode) {
		case 46 :
			ddValueGrid ? delDdValue() : delDd();
			break;
		case 13 :
			ddValueGrid ? endDdValueGridEditing() : endDdGridEditing();
			break;
		case 27 :
			ddValueGrid ? cancelEditDdValue() : cancelDdGridEdit();
			break;
		case 38 :
			if (ddValueGrid) {
				if (!endDdValueGridEditing()) {
					return;
				}
				var selected = ddValueGrid.datagrid("getSelected");
				if (!selected) {
					return;
				}
				var selectedIndex = ddValueGrid.datagrid('getRowIndex', selected);
				if (selectedIndex <= 0) {
					return;
				}
				endDdValueGridEditing();
				ddValueGrid.datagrid('selectRow', --selectedIndex);
			} else if (ddGrid) {
				if (!endDdGridEditing()) {
					return;
				}
				var selected = ddGrid.datagrid("getSelected");
				if (!selected) {
					return;
				}
				var selectedIndex = ddGrid.datagrid('getRowIndex', selected);
				if (selectedIndex <= 0) {
					return;
				}
				endDdGridEditing();
				ddGrid.datagrid('selectRow', --selectedIndex);
			}
			break;
		case 40 :
			if (ddValueGrid) {
				if (!endDdGridEditing()) {
					return;
				}
				if (ddValueGrid.datagrid('getRows').length <= 0) {
					openInsertDdValue();
					return;
				}
				var selected = ddValueGrid.datagrid("getSelected");
				if (!selected) {
					ddValueGrid.datagrid('selectRow', 0);
					return;
				}
				var selectedIndex = ddValueGrid.datagrid('getRowIndex', selected);
				if (selectedIndex == ddValueGrid.datagrid('getRows').length - 1) {
					openInsertDdValue();
				} else {
					ddValueGrid.datagrid('selectRow', ++selectedIndex);
				}
			} else if (ddGrid) {
				if (!endDdGridEditing()) {
					return;
				}
				if (ddGrid.datagrid('getRows').length <= 0) {
					openInsertDd();
					return;
				}
				var selected = ddGrid.datagrid("getSelected");
				if (!selected) {
					ddGrid.datagrid('selectRow', 0);
					return;
				}
				var selectedIndex = ddGrid.datagrid('getRowIndex', selected);
				if (selectedIndex == ddGrid.datagrid('getRows').length - 1) {
					openInsertDd();
				} else {
					ddGrid.datagrid('selectRow', ++selectedIndex);
				}
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
function showDdGridOptButtons(index) {
	$('#btnDdSave' + index + ',#btnDdCancel' + index).show();
}

/**
 * 显示编辑行最后一列的操作按钮
 * 
 * @param {}
 *            index 行索引
 */
function hideDdGridOptButtons(index) {
	$('#btnDdSave' + index + ',#btnDdCancel' + index).hide();
}

/**
 * 开始编辑行的毁掉函数
 * 
 * @param {}
 *            index 行索引
 * @param {}
 *            row 行数据
 */
function onBeginEditDdGrid(index, row) {
	showDdGridOptButtons(index);
	ddGrid.datagrid('resizeColumn', {
				field : 'notes',
				width : '30%'
			});

	ddGrid.datagrid('showColumn', 'opt');
	var editors = ddGrid.datagrid('getEditors', index);
	editors[0].target.trigger('focus');
}

function formatName(value, row, index) {
	return '<a href="javascript:void(0);" onclick="openDdValueWindow(' + row.id + ');">' + value + '</a>';
}

function formatDdGridOps(value, row, index) {
	var content = '<input type="button" id="btnDdSave' + index + '" style="margin:0px 4px;display:none;" value="保存" onclick="javascript:endDdGridEditing();">';
	content += '<input type="button" id="btnDdCancel' + index + '" style="margin:0px 4px;display:none;" value="取消" onclick="javascript:cancelDdGridEdit();">';
	return content;
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
function insertOrUpdate(index, row, changes) {
	var oldRecord;
	var url = '${contextPath!}/dataDictionary/';
	if (!row.id) {
		url += 'insert';
	} else {
		oldRecord = new Object();
		$.extend(true, oldRecord, row);
		url += 'update'
	}
	$.post(url, row, function(data) {
				if (data.code != 200) {
					if (oldRecord) {
						ddGrid.datagrid('updateRow', {
									index : index,
									row : oldRecord
								});
					} else {
						ddGrid.datagrid('deleteRow', index);
					}
					$.messager.alert('提示', data.result);
					return;
				}
				if (!row.id) {
					row.id = data.data.id;
					row.created = data.data.created;
				}
				row.modified = data.data.modified;
				ddGrid.datagrid('updateRow', {
							index : index,
							row : row
						});
				ddGrid.datagrid('refreshRow', index);
			}, 'json');
}

function formatLongToDate(value, row, index) {
	if (!value) {
		return '';
	}
	var now = new Date(value);
	var year = now.getFullYear();
	var month = now.getMonth() > 9 ? now.getMonth() + 1 : '0' + (now.getMonth() + 1);
	var date = now.getDate();
	var hour = now.getHours() > 10 ? now.getHours() : '0' + now.getHours();
	var minute = now.getMinutes() > 10 ? now.getMinutes() : '0' + now.getMinutes();
	var second = now.getSeconds() > 10 ? now.getSeconds() : '0' + now.getSeconds();
	return year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second;
}

/**
 * 单击含回调方法，逻辑是结束之前的行编辑模式
 * 
 * @param {}
 *            index
 * @param {}
 *            row
 */
function onClickDdGridRow(index, row) {
	if (endDdGridEditing()) {
		return;
	}
	if (ddGridEditIndex != index) {
		ddGrid.datagrid('endEdit', ddGridEditIndex);
		ddGridEditIndex = undefined;
	}
}

/**
 * 结束编辑回调函数
 * 
 * @param {}
 *            index 行索引
 * @param {}
 *            row 行数据
 * @param {}
 *            changes 当前行被修改的数据
 */
function onAfterEditDdGrid(index, row, changes) {
	ddGrid.datagrid('resizeColumn', [{
						field : 'notes',
						width : '40%'
					}]);
	hideDdGridOptButtons(index);
	ddGrid.datagrid('hideColumn', 'opt');
	ddGrid.datagrid('removeEditor', 'code');
	insertOrUpdate(index, row, changes);
}

/**
 * 取消行编辑回调方法
 * 
 * @param {}
 *            index
 * @param {}
 *            row
 */
function onCancelEditDdGrid(index, row) {
	if (!row.id) {
		ddGrid.datagrid('deleteRow', index);
	}
	ddGrid.datagrid('resizeColumn', [{
						field : 'notes',
						width : '40%'
					}]);
	hideDdGridOptButtons(index);
	ddGrid.datagrid('hideColumn', 'opt');
	ddGrid.datagrid('removeEditor', 'code');
}

function onClickDdGridRow(index, row) {
	if (endDdGridEditing()) {
		return;
	}
	ddGrid.datagrid('cancelEdit', editIndex);
	if (!row.id) {
		ddGrid.datagrid('deleteRow', editIndex);
		ddGridEditIndex = undefined;
	}
}

function onTreeLoadSuccess(node, data) {
	var selectedNode = ddValueTree.tree('getSelected');
	if (!selectedNode) {
		selectedNode = ddValueTree.tree('getRoot');
	}
	ddValueTree.tree('select', selectedNode.target);
	queryDdValueGrid(selectedNode.attributes.ddId);
}

function openDdValueWindow(ddId) {
	$('#win').window({
				title : '数据字典值列表',
				minimizable : false,
				maximizable : false,
				width : 900,
				height : 500,
				modal : true,
				href : '${contextPath}/dataDictionaryValue/list?ddId=' + ddId,
				onLoad : function() {
					window.ddId = ddId;
					ddValueGrid = $('#ddValueGrid');
					ddValueTree = $('#ddValueTree');
				},
				onClose : function() {
					ddValueGrid = undefined;
				}
			});
}

// 清空表单
function clearForm() {
	$('#form').form('clear');
}

/**
 * 绑定页面回车事件，以及初始化页面时的光标定位
 * 
 * @formId 表单ID
 * @elementName 光标定位在指点表单元素的name属性的值
 * @submitFun 表单提交需执行的任务
 */
$(function() {
			// 扩展datagrid:动态添加删除editor
			$.extend($.fn.datagrid.methods, {
						addEditor : function(jq, param) {
							if (param instanceof Array) {
								$.each(param, function(index, item) {
											var e = $(jq).datagrid('getColumnOption', item.field);
											e.editor = item.editor;
										});
							} else {
								var e = $(jq).datagrid('getColumnOption', param.field);
								e.editor = param.editor;
							}
						},
						removeEditor : function(jq, param) {
							if (param instanceof Array) {
								$.each(param, function(index, item) {
											var e = $(jq).datagrid('getColumnOption', item);
											e.editor = {};
										});
							} else {
								var e = $(jq).datagrid('getColumnOption', param);
								e.editor = {};
							}
						}
					});

			$.extend($.fn.datagrid.methods, {
						resizeColumn : function(jq, param) {
							return jq.each(function() {
										var dg = $(this);
										var fn = function(item) {
											var col = dg.datagrid('getColumnOption', item.field);
											col.width = item.width;
											if (typeof(col.width) == 'string') {
												var width = parseInt(col.width.replace('%', ''));
												col.boxWidth = col.boxWidth * width / 100;
											} else {
												col.boxWidth = col.width;
											}
											dg.datagrid('fixColumnSize', param.field);
										};
										if (param instanceof Array) {
											$(param).each(function(index, item) {
														fn.call(this, item);
													});
										} else {
											fn.call(this, param);
										}
									})
						}
					});

			window.ddGrid = $('#grid');
			if (document.addEventListener) {
				document.addEventListener("keyup", getKey, false);
			} else if (document.attachEvent) {
				document.attachEvent("onkeyup", getKey);
			} else {
				document.onkeyup = getKey;
			}
			queryDdGrid();
		});