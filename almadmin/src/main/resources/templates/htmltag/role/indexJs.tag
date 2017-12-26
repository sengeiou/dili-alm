// --------------------------------------------------------------------------------
var editIndex = undefined;

function dateFtt(fmt, date) {
	var o = {
		"M+" : date.getMonth() + 1, // 月份
		"d+" : date.getDate(), // 日
		"h+" : date.getHours(), // 小时
		"m+" : date.getMinutes(), // 分
		"s+" : date.getSeconds(), // 秒
		"q+" : Math.floor((date.getMonth() + 3) / 3), // 季度
		"S" : date.getMilliseconds()
		// 毫秒
	};
	if (/(y+)/.test(fmt))
		fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
	for (var k in o)
		if (new RegExp("(" + k + ")").test(fmt))
			fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
	return fmt;
}

function getHyperlinkContext(text, handler, data) {
	return '<span style="padding:5px;"><a href="javascript:void(0)" onclick="' + handler + '(' + data + ')">' + text + '</a></span>'
}

function isEditing() {
	return undefined != editIndex;
}

function beginEditing(index) {
	if (undefined == editIndex) {
		roleGrid.datagrid('beginEdit', index);
		editIndex = index;
		return true;
	}

	return false;
}

function endEditing() {
	if (undefined == editIndex) {
		return true
	}

	if (roleGrid.datagrid('validateRow', editIndex)) {
		roleGrid.datagrid('endEdit', editIndex);
		editIndex = undefined;
		return true;
	}

	return false;
}

function cancelEditing() {
	if (undefined != editIndex) {
		roleGrid.datagrid('cancelEdit', editIndex);
		editIndex = undefined;
	}
}

function getRowById(id) {
	var rows = roleGrid.datagrid("getRows");
	for (var i in rows) {
		if (rows[i].id == id) {
			return rows[i];
		}
	}

	return null;
}

function onAddClicked() {
	if (!dataAuth.addRole) {
		return false;
	}
	if (!endEditing()) {
		$.messager.alert('警告', '有数据正在进行编辑');
		return;
	}

	var index = roleGrid.datagrid('getRows').length;
	roleGrid.datagrid('appendRow', {});
	if (!beginEditing(index)) {
		$.messager.alert('警告', '有数据正在进行编辑');
	}
}

function onRemoveClicked(id) {
	if (!dataAuth.removeRole) {
		return false;
	}
	if (!endEditing()) {
		$.messager.alert('警告', '有数据正在进行编辑');
		return;
	}

	var selected = null == id ? roleGrid.datagrid("getSelected") : getRowById(id);
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}

	$.messager.confirm('确认', '您确认想要删除该角色吗？', function(r) {
				if (r) {
					var index = roleGrid.datagrid("getRowIndex", selected);
					requestDelete(index, {
								id : selected.id
							}, function(index) {
								try {
									LogUtils.saveLog("删除角色:" + selected.id, function() {
											});
								} catch (e) {
									$.messager.alert('错误', e);
								}
								roleGrid.datagrid("deleteRow", index);
							});
					// 强制刷新 formatOptions() 中的 index
					// roleGrid.datagrid("loadData",roleGrid.datagrid("getRows"));;
				}
			});
}

function onPageUpClicked() {
	if (!endEditing()) {
		$.messager.alert('警告', '有数据正在进行编辑');
		return;
	}

	var maxRowIndex = roleGrid.datagrid("getRows").length - 1;
	var selected = roleGrid.datagrid("getSelected");
	if (null == selected) {
		if (0 <= maxRowIndex) {
			roleGrid.datagrid("selectRow", maxRowIndex);
		}
		// maxRownIndex 小于0 表示datagrid没有数据
		return;
	}

	var index = roleGrid.datagrid("getRowIndex", selected) - 1;
	roleGrid.datagrid("selectRow", index < 0 ? maxRowIndex : index);
}

function onPageDownClicked() {
	if (!endEditing()) {
		$.messager.alert('警告', '有数据正在进行编辑');
		return;
	}

	var selected = roleGrid.datagrid("getSelected");
	if (null == selected) {
		onAddClicked();
		return;
	}

	var maxRowIndex = roleGrid.datagrid("getRows").length - 1;
	var index = roleGrid.datagrid("getRowIndex", selected) + 1;
	if (index > maxRowIndex) {
		onAddClicked();
	} else {
		roleGrid.datagrid("selectRow", index);
	}
}

function onEditClicked(id) {
	if (!dataAuth.editRole) {
		return false;
	}
	if (!endEditing()) {
		$.messager.alert('警告', '有数据正在进行编辑');
		return;
	}

	var selected = null == id ? roleGrid.datagrid("getSelected") : getRowById(id);
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}

	var index = roleGrid.datagrid("getRowIndex", selected);
	if (!beginEditing(index)) {
		$.messager.alert('警告', '有数据正在进行编辑');
	}
}

function onSaveClicked(id) {
	if (!dataAuth.saveRole) {
		return false;
	}
	if (!endEditing()) {
		$.messager.alert('警告', '无法保存编辑后的数据，请检查格式');
	}
}

function onCancelClicked(id) {
	if (!dataAuth.cancelSaveRole) {
		return false;
	}
	cancelEditing();
}

function onDblClickRow(index, row) {
	if (!dataAuth.editRole) {
		return false;
	}
	onEditClicked();
}

function onBeforeEdit(index, row) {
	row.editing = true;
	roleGrid.datagrid('refreshRow', index);
}

function onAfterEdit(index, row, changes) {
	row.editing = false;

	requestSave(index, row, function(index, retData) {
				if (!row.id) {
					try {
						LogUtils.saveLog("新增角色:" + retData.id, function() {
								});
					} catch (e) {
						$.messager.alert('错误', e);
					}
				} else {
					try {
						LogUtils.saveLog("修改角色:" + retData.id, function() {
								});
					} catch (e) {
						$.messager.alert('错误', e);
					}
				}
				if (retData) {
					var data = eval(retData);
					data.created = dateFtt("yyyy-MM-dd hh:mm:ss", null == data.created ? new Date() : new Date(data.created));
					data.modified = dateFtt("yyyy-MM-dd hh:mm:ss", null == data.modified ? new Date() : new Date(data.modified));
					roleGrid.datagrid("updateRow", {
								index : index,
								row : data
							});
				}
			});

	roleGrid.datagrid('refreshRow', index);

	for (var field in changes) {
		roleGrid.datagrid("autoSizeColumn", field)
	}
}

function onCancelEdit(index, row) {
	row.editing = false;
	if (row && row.id) {
		roleGrid.datagrid('refreshRow', index);
	} else {
		roleGrid.datagrid('deleteRow', index);
	}
}

function onUserListClicked(roleid) {
	if (!dataAuth.viewRoleUserList) {
		return false;
	}
	var selected = getRowById(roleid);
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}

	roleGrid.datagrid("selectRow", roleGrid.datagrid("getRowIndex", selected));
	var opts = $('#userListGrid').datagrid("options");
	if (null == opts.url || "" == opts.url) {
		opts.url = "${contextPath!}/user/findUserByRole";
	}

	var role = {
		id : roleid
	};
	$("#userListGrid").datagrid("load", role);

	$('#userListDlg').dialog('open');
	$('#userListDlg').dialog('center');
}

function unbindRoleUser(id) {
	if (!dataAuth.unBindRoleUser) {
		return false;
	}
	var selected = null;
	var rows = $('#userListGrid').datagrid("getRows");
	for (var i in rows) {
		if (rows[i].id == id) {
			selected = rows[i];
		}
	}

	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}

	var role = roleGrid.datagrid("getSelected");
	var roleId = role.id;
	var userList = [];
	userList.push(selected.id);
	var formData = {
		roleId : roleId,
		userIds : userList
	};

	$.messager.confirm('确认', '您确认想要解绑该用户吗？', function(r) {
				if (r) {
					var index = $('#userListGrid').datagrid("getRowIndex", selected);
					requestUnbind(index, JSON.stringify(formData), function(index) {
								try {
									LogUtils.saveLog("解除绑定用户:" + JSON.stringify(formData), function() {
											});
								} catch (e) {
									$.messager.alert('错误', e);
								}
								$('#userListGrid').datagrid("deleteRow", index);
							});
				}
			});
}

// --------------------------------------------------------------------------------
function requestUnbind(index, data, callback) {
	$.ajax({
				type : "POST",
				url : "${contextPath!}/role/unbindRoleUser",
				data : data,
				contentType : 'application/json',
				dataType : "json",
				processData : true,
				async : true,
				success : function(retData) {
					if (retData.code == "200") {
						(callback && typeof(callback) === "function") && callback(index);
					} else {
						$.messager.alert('错误', retData.result);
					}
				},
				error : function() {
					$.messager.alert('错误', '远程访问失败');
				}
			});
}

function requestDelete(index, data, callback) {
	var url = "${contextPath!}/role/deleteIfUserNotBind";

	$.ajax({
				type : "POST",
				url : url,
				data : data,
				dataType : "json",
				processData : true,
				async : true,
				success : function(retData) {
					if (retData.code == "200") {
						(callback && typeof(callback) === "function") && callback(index);
						roleGrid.datagrid('acceptChanges');
					} else {
						roleGrid.datagrid('rejectChanges');
						$.messager.alert('错误', retData.result);
					}
				},
				error : function() {
					roleGrid.datagrid('rejectChanges');
					$.messager.alert('错误', '远程访问失败');
				}
			});
}

function requestSave(index, data, callback) {
	var url = null;
	if (data.id == null || data.id == "") {
		// 没有id就新增
		url = "${contextPath!}/role/insert";
	} else {
		// 有id就修改
		url = "${contextPath!}/role/update";
	}

	$.ajax({
				type : "POST",
				url : url,
				data : data,
				dataType : "json",
				processData : true,
				async : true,
				success : function(retData) {
					if (retData.code == "200") {
						(callback && typeof(callback) === "function") && callback(index, retData.data);
						roleGrid.datagrid('acceptChanges');
					} else {
						roleGrid.datagrid('rejectChanges');
						$.messager.alert('错误', retData.result);
					}
				},
				error : function() {
					roleGrid.datagrid('rejectChanges');
					$.messager.alert('错误', '远程访问失败');
				}
			});
}

// 表格查询
function queryGrid() {
	var opts = roleGrid.datagrid("options");
	if (null == opts.url || "" == opts.url) {
		opts.url = "${contextPath!}/role/listPage";
	}
	if (!$('#form').form("validate")) {
		return;
	}
	var param = bindMetadata("roleGrid");
	var formData = $("#form").serializeObject();
	$.extend(formData, param);
	roleGrid.datagrid("load", formData);
}

// --------------------------------------------------------------------------------

// 表格表头右键菜单
function headerContextMenu(e, field) {
	e.preventDefault();
	if (!cmenu) {
		createColumnMenu("roleGrid");
	}
	cmenu.menu('show', {
				left : e.pageX,
				top : e.pageY
			});
}

// 全局按键事件
function getKey(e) {
	e = e || window.event;
	var keycode = e.which ? e.which : e.keyCode;
	// alert(keycode);
	if (46 == keycode) {
		if (!dataAuth.removeRole) {
			return;
		}
		// DELETE
		if (isEditing())
			return;
		onRemoveClicked();
	} else if (38 == keycode) {
		// PageUp
		if (isEditing())
			return;
		onPageUpClicked();
	} else if (40 == keycode) {
		// PageDown
		if (isEditing())
			return;
		onPageDownClicked();
	} else if (13 == keycode) {
		if (!dataAuth.saveRole) {
			return;
		}
		// ENTER
		onSaveClicked();
	} else if (27 == keycode) {
		if (!dataAuth.cancelSaveRole) {
			return;
		}
		// ESC
		onCancelClicked();
	} else if (45 == keycode) {
		if (!dataAuth.editRole) {
			return;
		}
		// INSERT
		onEditClicked();
	}
}

loadTree = function(roleId, type) {
	$.get('${contextPath!}/dataAuth/editRoleDataAuth.json', {
				roleId : roleId,
				type : type
			}, function(data) {
				if (data.code != 200) {
					$.messager.alert('提示', data.result);
				}
				var znodes = data.data.allDataAuths;
				var roleDataAuths = data.data.roleDataAuths;
				$.each(znodes, function(index, node) {
							$.each(roleDataAuths, function(i, n) {
										if (node.id == n.id) {
											node.checked = true;
										}
									});
						});
				$.fn.zTree.init($("#tree"), {
							check : {
								enable : true
							},
							data : {
								simpleData : {
									enable : true,
									idKey : 'id',
									pIdKey : 'parentId',
									rootPId : -1
								}
							}
						}, znodes);
			}, 'json');
};

saveRoleDataAuth = function() {
	var tree = $.fn.zTree.getZTreeObj("tree");
	var checkedNodes = tree.getCheckedNodes();
	var submitData = {
		roleId : roleId,
		type : dataAuthType,
		dataAuthIds : []

	};
	$.each(checkedNodes, function(index, node) {
				submitData.dataAuthIds.push(node.id);
			});
	$.ajax({
				type : "POST",
				url : '${contextPath!}/dataAuth/updateRoleDataAuth.json',
				contentType : "application/json; charset=utf-8",
				data : JSON.stringify(submitData),
				dataType : "json",
				success : function(data) {
					if (data.code == 200) {
						$.messager.alert('提示', '保存成功');
					} else {
						$.messager.alert('提示', data.result);
					}
				},
				error : function(message) {
					$("#request-process-patent").html("提交数据失败！");
				}
			});
};

onTypeChange = function(newVal, oldVal) {
	loadTree(roleId, newVal);
};

$(function() {
			window.saveRoleMenuResource = function() {
				var tree = $.fn.zTree.getZTreeObj("tree");
				var checkedNodes = tree.getCheckedNodes();
				var submitData = {
					roleId : roleId,
					menuResources : []

				};
				$.each(checkedNodes, function(index, node) {
							submitData.menuResources.push({
										id : node.id,
										resource : node.resource,
										resourceId : node.resourceId
									});
						});
				$.ajax({
							type : "POST",
							url : '${contextPath!}/role/updateRoleMenuResource',
							contentType : "application/json; charset=utf-8",
							data : JSON.stringify(submitData),
							dataType : "json",
							success : function(data) {
								if (data.code == 200) {
									$.messager.alert('提示', '保存成功');
								} else {
									$.messager.alert('提示', data.result);
								}
							},
							error : function(message) {
								$("#request-process-patent").html("提交数据失败！");
							}
						});
			};

			window.editRoleMenuAndResource = function(roleId) {
				if (!dataAuth.viewRoleAuth) {
					return false;
				}
				$('#win').window({
							title : '角色权限',
							width : 900,
							height : 500,
							href : '${contextPath!}/role/roleMenuAndResource.html?roleId=' + roleId,
							onLoad : function() {
								window.roleId = $('#roleId').val();
								$.get('${contextPath!}/role/roleMenuAndResource.json', {
											roleId : roleId
										}, function(data) {
											if (data.code != 200) {
												$.messager.alert('提示', data.result);
											}
											var znodes = data.data.allMenus;
											var roleMenus = data.data.roleMenus;
											$.each(znodes, function(index, node) {
														$.each(roleMenus, function(i, n) {
																	if (node.id && node.id == n.id) {
																		node.checked = true;
																	} else if (node.resourceId && node.resourceId == n.resourceId) {
																		node.checked = true;
																	}
																});
													});
													
											$.fn.zTree.init($("#tree"), {
														check : {
															enable : true
														},
														data : {
															simpleData : {
																enable : true,
																idKey : 'id',
																pIdKey : 'parentId',
																rootPId : -1
															}
														},
														callback : {
															beforeCheck:function (event,treeId, treeNode) {
																if(treeId.id==126){
																	$.messager.alert('警告', '首页是必选项，不可编辑');
																	return  false;
																	
																}
																return  true;
															},
															onNodeCreated : function(event, treeId, treeNode) {
																if (treeNode.resourceId) {
																	$("#" + treeNode.tId).addClass("menuRes")
																} else {
																	$("#" + treeNode.tId).addClass("clearfix")
																}

															}
														}

													}, znodes);
												var treeObj = $.fn.zTree.getZTreeObj("tree");
												var node = treeObj.getNodeByParam("id", "126");
												treeObj.checkNode(node, true, true);
												
										}, 'json');
							}
						});
			};
			function beforeCheck(treeId, treeNode) {
				
			return (treeNode.doCheck !== false);
			}
			window.editRoleDataAuth = function(roleId) {
				$('#win').window({
							title : '编辑数据权限',
							minimizable : false,
							maximizable : false,
							width : 900,
							height : 500,
							href : '${contextPath!}/dataAuth/editRoleDataAuth.html?roleId=' + roleId,
							onLoad : function() {
								window.roleId = $('#roleId').val();
								window.dataAuthType = $('#dataAuthType').val();
								loadTree(roleId, dataAuthType);
							}
						});
			};

			bindFormEvent("form", "roleName", queryGrid);
			if (document.addEventListener) {
				document.addEventListener("keyup", getKey, false);
			} else if (document.attachEvent) {
				document.attachEvent("onkeyup", getKey);
			} else {
				document.onkeyup = getKey;
			}

			window.roleGrid = $('#roleGrid');
			queryGrid();
		});