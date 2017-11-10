var gridType;
// 编辑行索引
var editIndexMap = {
	grid : undefined,
	inlineGrid : undefined
};

function isEditing(gridId) {
	return undefined != editIndexMap[gridId];
}

// 结束行编辑
function endEditing(gridId) {
	var grid = $('#' + gridId);
	if (editIndexMap[gridId] == undefined) {
		return true
	}
	if (grid.datagrid('validateRow', editIndexMap[gridId])) {
		grid.datagrid('endEdit', editIndexMap[gridId]);
		editIndexMap[gridId] = undefined;
		return true;
	} else {
		return false;
	}
}

// 新增一行空数据并开启编辑模式
function openInsert(gridId) {
	if (!dataAuth.addMenu) {
		return;
	}
	var grid = $('#' + gridId);
	if (!endEditing()) {
		$.messager.alert('警告', '有数据正在编辑');
		return;
	}
	editIndexMap[gridId] = grid.datagrid('getRows').length;
	grid.datagrid('appendRow', {});
	grid.datagrid('selectRow', editIndexMap[gridId]);
	grid.datagrid('beginEdit', editIndexMap[gridId]);
}

// 开启选中行的编辑模式
function openUpdate(gridId) {
	if (!dataAuth.editMenu) {
		return;
	}
	var grid = $('#' + gridId);
	var selected = grid.datagrid("getSelected");
	if (!selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	var index = grid.datagrid('getRowIndex', selected);
	if (endEditing(gridId)) {
		grid.datagrid('selectRow', index).datagrid('beginEdit', index);
		editIndexMap[gridId] = index;
	}
}

// 根据主键删除
function del(gridId) {
	if (!dataAuth.deleteMenu) {
		return;
	}
	deleting = true;
	var grid = $('#' + gridId);
	var selected = grid.datagrid("getSelected");
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	$.messager.confirm('确认', '您确认想要删除记录吗？', function(r) {
				if (r) {
					$.ajax({
								type : "POST",
								url : (gridType == 1 && gridId == 'grid' ? '${contextPath!}/resource/' : '${contextPath!}/menu/') + '/delete',
								data : {
									id : selected.id
								},
								processData : true,
								dataType : "json",
								async : true,
								success : function(data) {
									if (data.code == "200") {
										grid.datagrid('deleteRow', grid.datagrid('getRowIndex', selected));
										$('#dlg').dialog('close');
										var removeNode = menuTree.tree('find', selected.id);
										if (removeNode) {
											menuTree.tree('remove', removeNode.target);
										}
									} else {
										$.messager.alert('错误', data.result);
									}
								},
								error : function() {
									$.messager.alert('错误', '远程访问失败');
								}
							});
				}
				deleting = undefined;
			});
}

/**
 * 查询表格数据，因为menu下面包含resource，所以menu和resource的列头是不一样的
 * 
 * @param {}
 *            node 页面布局左边树形菜单节点数据，根据这个参数来判断是显示menu还是resource
 */
function queryGrid(node) {
	cancelEdit('grid');
	cancelEdit('inlineGrid');
	gridType = node.attributes.type;
	if (gridType == 1 || gridType == 2) {
		$('#p1').panel('resize', {
					height : '50%'
				});
		$('#p2').panel('resize', {
					height : '50%'
				});
		$('#p1').panel('open');
		$('#p2').panel('open');
		menuGrid.datagrid({
					title : "权限列表",
					fitColumns : true,
					remoteSort : false,
					loadMsg : "数据加载中...",
					singleSelect : true,
					method : "post",
					multiSort : true,
					sortName : "id",
					sortOrder : 'asc',
					align : "center",
					height : '100%',
					striped : true,
					onClickRow : onClickRow,
					onDblClickRow : onDblClickRow,
					onAfterEdit : onAfterEdit,
					onCancelEdit : onCancelEdit,
					onBeginEdit : onBeginEdit,
					onEndEdit : onEndEdit,
					onLoadSuccess : onGridLoadSuccess,
					idField : "id",
					url : '${contextPath!}/resource/list',
					toolbar : [{
								iconCls : 'icon-add',
								plain : true,
								handler : function() {
									openInsert('grid');
								}
							}, {
								iconCls : 'icon-edit',
								plain : true,
								handler : function() {
									openUpdate('grid');
								}
							}, {
								iconCls : 'icon-remove',
								plain : true,
								handler : function() {
									del('grid');
								}
							}],
					queryParams : {
						menuId : node.id
					},
					columns : [[{
								field : 'id',
								title : 'id',
								hidden : true
							}, {
								field : 'name',
								title : '权限名称',
								width : '20%',
								editor : {
									type : 'textbox',
									options : {
										required : true,
										missingMessage : '请输入权限名称'
									}
								}
							}, {
								field : 'code',
								title : '权限代码',
								width : '20%',
								editor : {
									type : 'textbox',
									options : {
										required : true,
										missingMessage : '请输入权限代码'
									}
								}
							}, {
								field : 'description',
								title : '描述',
								width : '60%',
								editor : 'textbox'

							}, {
								field : 'opt',
								title : '操作',
								width : '10%',
								hidden : true,
								formatter : function(value, row, index) {
									var content = '<input type="button" id="gridBtnSave' + index + '" style="margin:0px 4px;display:none;" value="保存" onclick="javascript:endEditing(\'grid\');">';
									content += '<input type="button" id="gridBtnCancel' + index + '" style="margin:0px 4px;display:none;" value="取消" onclick="javascript:cancelEdit(\'grid\');">';
									return content;
								}
							}]]
				});
		inlineGrid.datagrid({
					title : "内链菜单列表",
					fitColumns : true,
					remoteSort : false,
					loadMsg : "数据加载中...",
					singleSelect : true,
					method : "post",
					multiSort : true,
					sortName : "id",
					sortOrder : 'asc',
					align : "center",
					height : '100%',
					striped : true,
					onClickRow : onClickRow,
					onDblClickRow : onDblClickRow,
					onAfterEdit : onAfterEdit,
					onCancelEdit : onCancelEdit,
					onBeginEdit : onBeginEdit,
					onEndEdit : onEndEdit,
					onLoadSuccess : onGridLoadSuccess,
					idField : "id",
					url : '${contextPath!}/menu/list',
					toolbar : [{
								iconCls : 'icon-add',
								plain : true,
								handler : function() {
									openInsert('inlineGrid');
								}
							}, {
								iconCls : 'icon-edit',
								plain : true,
								handler : function() {
									openUpdate('inlineGrid');
								}
							}, {
								iconCls : 'icon-remove',
								plain : true,
								handler : function() {
									del('inlineGrid');
								}
							}],
					queryParams : {
						parentId : node.id,
						type : 2
					},
					columns : [[{
								field : 'id',
								title : 'id',
								hidden : true
							}, {
								field : 'name',
								title : '菜单名称',
								width : '10%',
								editor : {
									type : 'textbox',
									options : {
										required : true,
										validType : 'length[1, 50]',
										missingMessage : '菜单名称不能为空',
										invalidMessage : '菜单名称必须是1-50个字符'
									}
								}
							}, {
								field : 'type',
								title : '类型',
								width : '10%',
								formatter : function(value, row, index) {
									if (value == 0) {
										return '目录';
									} else if (value == 1) {
										return '链接';
									} else if (value == 2) {
										return '内联';
									}
								},
								editor : {
									type : 'combobox',
									options : {
										required : true,
										missingMessage : '请选择连接类型',
										valueField : 'value',
										textField : 'name',
										editable : false,
										data : [{
													name : '内联',
													value : 2
												}]
									}
								}

							}, {
								field : 'menuUrl',
								title : '菜单链接地址',
								width : '35%',
								editor : {
									type : 'textbox'
								}
							}, {
								field : 'description',
								title : '描述',
								width : '40%',
								editor : 'text'
							}, {
								field : 'orderNumber',
								title : '排序',
								width : '5%',
								editor : {
									type : 'numberbox'
								}
							}, {
								field : 'opt',
								title : '操作',
								width : '10%',
								hidden : true,
								formatter : function(value, row, index) {
									var content = '<input type="button" id="inlineGridBtnSave' + index
											+ '" style="margin:0px 4px;display:none;" value="保存" onclick="javascript:endEditing(\'inlineGrid\');">';
									content += '<input type="button" id="inlineGridBtnCancel' + index
											+ '" style="margin:0px 4px;display:none;" value="取消" onclick="javascript:cancelEdit(\'inlineGrid\');">';
									return content;
								}
							}]]
				});
	} else if (gridType == 0) {
		$('#p2').panel('close');
		$('#p1').panel('open');
		$('#p1').panel('resize', {
					height : '100%'
				});
		menuGrid.datagrid({
					title : "菜单列表",
					fitColumns : true,
					remoteSort : false,
					loadMsg : "数据加载中...",
					singleSelect : true,
					method : "post",
					multiSort : true,
					sortName : "orderNumber",
					sortOrder : 'asc',
					align : "center",
					height : '100%',
					striped : true,
					onClickRow : onClickRow,
					onDblClickRow : onDblClickRow,
					onAfterEdit : onAfterEdit,
					onCancelEdit : onCancelEdit,
					onBeginEdit : onBeginEdit,
					onEndEdit : onEndEdit,
					onLoadSuccess : onGridLoadSuccess,
					idField : "id",
					url : '${contextPath!}/menu/list',
					toolbar : [{
								iconCls : 'icon-add',
								plain : true,
								handler : function() {
									openInsert('grid');
								}
							}, {
								iconCls : 'icon-edit',
								plain : true,
								handler : function() {
									openUpdate('grid');
								}
							}, {
								iconCls : 'icon-remove',
								plain : true,
								handler : function() {
									del('grid');
								}
							}],
					queryParams : {
						parentId : node.id
					},
					columns : [[{
								field : 'id',
								title : 'id',
								hidden : true
							}, {
								field : 'name',
								title : '菜单名称',
								width : '10%',
								editor : {
									type : 'textbox',
									options : {
										required : true,
										validType : 'length[1, 50]',
										missingMessage : '菜单名称不能为空',
										invalidMessage : '菜单名称必须是1-50个字符'
									}
								}
							}, {
								field : 'type',
								title : '类型',
								width : '10%',
								formatter : function(value, row, index) {
									if (value == 0) {
										return '目录';
									} else if (value == 1) {
										return '链接';
									} else if (value == 2) {
										return '内联';
									}
								},
								editor : {
									type : 'combobox',
									options : {
										required : true,
										missingMessage : '请选择连接类型',
										valueField : 'value',
										textField : 'name',
										editable : false,
										data : [{
													name : '目录',
													value : 0
												}, {
													name : '链接',
													value : 1
												}]
									}
								}

							}, {
								field : 'menuUrl',
								title : '菜单链接地址',
								width : '35%',
								editor : {
									type : 'textbox'
								}
							}, {
								field : 'description',
								title : '描述',
								width : '40%',
								editor : 'text'
							}, {
								field : 'orderNumber',
								title : '排序',
								width : '5%',
								editor : {
									type : 'numberbox'
								}
							}, {
								field : 'opt',
								title : '操作',
								width : '10%',
								hidden : true,
								formatter : function(value, row, index) {
									var content = '<input type="button" id="gridBtnSave' + index + '" style="margin:0px 4px;display:none;" value="保存" onclick="javascript:endEditing(\'grid\');">';
									content += '<input type="button" id="gridBtnCancel' + index + '" style="margin:0px 4px;display:none;" value="取消" onclick="javascript:cancelEdit(\'grid\');">';
									return content;
								}
							}]]
				});
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
	openUpdate(this.id);
}

/**
 * 显示编辑行最后一列的操作按钮
 * 
 * @param {}
 *            index 行索引
 */
function showOptButtons(gridId, index) {
	$('#' + gridId + 'BtnSave' + index + ',#' + gridId + 'BtnCancel' + index).show();
}

/**
 * 显示编辑行最后一列的操作按钮
 * 
 * @param {}
 *            index 行索引
 */
function hideOptButtons(gridId, index) {
	$('#' + gridId + 'BtnSave' + index + ',#' + gridId + 'BtnCancel' + index).hide();
}

/**
 * 开始编辑行的毁掉函数
 * 
 * @param {}
 *            index 行索引
 * @param {}
 *            row 行数据
 */
function onBeginEdit(index, row) {
	var grid = $('#' + this.id);
	showOptButtons(this.id, index);
	if (gridType != 0 && this.id == 'grid') {
		grid.datagrid('resizeColumn', {
					field : 'description',
					width : '40%'
				});
	} else {
		grid.datagrid('resizeColumn', [{
							field : 'menuUrl',
							width : '30%'
						}, {
							field : 'description',
							width : '35%'
						}]);
	}

	grid.datagrid('showColumn', 'opt');
	var editors = grid.datagrid('getEditors', index);
	editors[0].target.trigger('focus');
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
function onAfterEdit(index, row, changes) {
	debugger;
	var grid = $('#' + this.id);
	var isValid = grid.datagrid('validateRow', index);
	if (!isValid) {
		return false;
	}
	hideOptButtons(this.id, index);
	var selectedTreeNode = menuTree.tree('getSelected');
	if (gridType != 0 && this.id == 'grid') {
		insertOrUpdateResource(this.id, selectedTreeNode, index, row, changes);
	} else {
		insertOrUpdateMenu(this.id, selectedTreeNode, index, row, changes);
	}
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
function insertOrUpdateMenu(gridId, node, index, row, changes) {
	var oldRecord;
	var url = '${contextPath!}/menu/';
	var parentId = node.id;
	if (!row.id) {
		row.parentId = parentId;
		url += 'insert';
	} else {
		oldRecord = new Object();
		$.extend(true, oldRecord, row);
		url += 'update'
	}
	$.post(url, row, function(data) {
				var grid = $('#' + gridId);
				if (data.code != 200) {
					if (oldRecord) {
						grid.datagrid('updateRow', {
									index : index,
									row : oldRecord
								});
					} else {
						grid.datagrid('deleteRow', index);
					}
					$.messager.alert('提示', data.result);
					return;
				}
				if (!row.id) {
					var node = menuTree.tree('getSelected');
					row.id = data.data.id;
					menuTree.tree('append', {
								parent : node.target,
								data : [{
											id : row.id,
											parentId : row.parentId,
											name : row.name,
											attributes : {
												type : row.type
											}
										}]
							});
				} else {
					var node = menuTree.tree('find', row.id);
					menuTree.tree('update', {
								target : node.target,
								text : row.name
							});
				}
				grid.datagrid('updateRow', {
							index : index,
							row : row
						});
				if (changes && changes.orderNumber) {
					grid.datagrid('sort', {
								sortName : 'orderNumber',
								sortOrder : 'asc'
							});
				}
				grid.datagrid('refreshRow', index);
			}, 'json');
}

/**
 * 插入或者修改资源信息
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
function insertOrUpdateResource(gridId, node, index, row, changes) {
	var oldRecord;
	var url = '${contextPath!}/resource/';
	var menuId = node.id;
	if (!row.id) {
		row.menuId = menuId;
		url += 'insert';
	} else {
		url += 'update'
		oldRecord = new Object();
		$.extend(true, oldRecord, row);
	}
	$.post(url, row, function(data) {
				var grid = $('#' + gridId);
				if (data.code != 200) {
					if (oldRecord) {
						grid.datagrid('updateRow', {
									index : index,
									row : oldRecord
								});
					} else {
						grid.datagrid('deleteRow', index);
					}
					$.messager.alert('提示', data.result);
					return;
				}
				if (!row.id) {
					row.id = data.data.id;
				}
				grid.datagrid('updateRow', {
							index : index,
							row : row
						});
				grid.datagrid('refreshRow', index);
			}, 'json');
}

var selectedTreeId = undefined;
/**
 * 页面加载完毕后默认选中菜单树的根节点节点
 * 
 * @param {}
 *            node
 * @param {}
 *            data
 */
function onLoadSuccess(node, data) {
	if (!selectedTreeId) {
		selectedTreeId = -1;
	}
	var selectedNode = menuTree.tree("find", selectedTreeId);
	menuTree.tree("select", selectedNode.target);
	queryGrid(selectedNode);
};

/**
 * 单击含回调方法，逻辑是结束之前的行编辑模式
 * 
 * @param {}
 *            index
 * @param {}
 *            row
 */
function onClickRow(index, row) {
	var grid = $('#' + this.id);
	if (editIndexMap[this.id] == index) {
		return;
	}
	if (endEditing(this.id)) {
		return;
	}
	grid.datagrid('cancelEdit', editIndexMap[this.id]);
	if (!row.id) {
		grid.datagrid('deleteRow', editIndexMap[this.id]);
		editIndex = undefined;
	}
}

/**
 * 取消行编辑
 */
function cancelEdit(gridId) {
	var grid = $('#' + gridId);
	if (editIndexMap[gridId] == undefined) {
		return;
	}
	grid.datagrid('cancelEdit', editIndexMap[gridId]);
	editIndexMap[gridId] = undefined;
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
	var grid = $('#' + this.id);
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

/**
 * 结束编辑回调方法
 * 
 * @param {}
 *            index
 * @param {}
 *            row
 */
function onEndEdit(index, row) {
	var grid = $('#' + this.id);
	hideOptButtons(this.id, index);
	if (gridType != 0 && this.id == 'grid') {
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

function onGridLoadSuccess() {
	$(this).datagrid('keyCtr');
}

var deleting = undefined;
$.extend($.fn.datagrid.methods, {
			keyCtr : function(jq) {
				return jq.each(function() {
							var grid = $(this);
							var gridId = this.id;
							grid.datagrid('getPanel').panel('panel').attr('tabindex', 1).bind('keydown', function(e) {
										switch (e.keyCode) {
											case 46 :
												if (isEditing(gridId))
													return;
												var selected = grid.datagrid("getSelected");
												if (selected && selected != null) {
													if (deleting) {
														return;
													}
													del(gridId);
												}
												break;
											case 13 :
												endEditing(gridId);
												break;
											case 27 :
												cancelEdit(gridId);
												break;
											case 38 :
												if (!endEditing(gridId)) {
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
												if (!endEditing(gridId)) {
													return;
												}
												if (grid.datagrid('getRows').length <= 0) {
													openInsert(gridId);
													return;
												}
												var selected = grid.datagrid("getSelected");
												if (!selected) {
													grid.datagrid('selectRow', 0);
													return;
												}
												var selectedIndex = grid.datagrid('getRowIndex', selected);
												if (selectedIndex == grid.datagrid('getRows').length - 1) {
													openInsert(gridId);
												} else {
													grid.datagrid('selectRow', ++selectedIndex);
												}
												break;
										}
									});
						});
			}
		});
/**
 * 绑定页面回车事件，以及初始化页面时的光标定位
 * 
 * @formId 表单ID
 * @elementName 光标定位在指点表单元素的name属性的值
 * @submitFun 表单提交需执行的任务
 */
$(function() {

	window.menuGrid = $('#grid');
	window.inlineGrid = $('#inlineGrid');
	window.menuTree = $('#menuTree');
		// if (document.addEventListener) {
		// document.addEventListener("keyup", getKey, false);
		// } else if (document.attachEvent) {
		// document.attachEvent("onkeyup", getKey);
		// } else {
		// document.onkeyup = getKey;
		// }

	});