function getHyperlinkContext(text, handler, data) {
	return '<span style="padding:5px;"><a href="javascript:void(0)" onclick="' + handler + '(' + data + ')">' + text + '</a></span>'
}

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

$.extend($.fn.validatebox.defaults.rules, {
			pwdEquals : {
				validator : function(value, param) {
					return value == $(param[0]).val();
				},
				message : '新密码不一致'
			},
			cellphone : {
				validator : function(value, param) {
					return (/^1[3|4|5|7|8][0-9]\d{4,8}$/.test(value))
				},
				message : '请输入正确的手机号码'
			}
		});

function loadData4DataList(datalistid, url, params) {
	if (null == url) {
		$("#" + datalistid).datalist("loadData", {
					total : 0,
					rows : []
				});
		return;
	}

	$.ajax({
				type : "POST",
				url : url,
				data : params,
				dataType : "json",
				processData : true,
				async : true,
				success : function(data) {
					if (data.code == "200") {
						var roleList = data.data;
						if (null != roleList && 0 < roleList.length) {
							$("#" + datalistid).datalist("loadData", {
										total : roleList.length,
										rows : roleList
									});
							return;
						}
					} else {
						$.messager.alert('错误', data.result);
					}
					$("#" + datalistid).datalist("loadData", {
								total : 0,
								rows : []
							});
				},
				error : function() {
					$("#" + datalistid).datalist("loadData", {
								total : 0,
								rows : []
							});
					$.messager.alert('错误', '远程访问失败');
				}
			});
}

function getRowById(id) {
	var rows = userGrid.datagrid("getRows");
	for (var i in rows) {
		if (rows[i].id == id) {
			return rows[i];
		}
	}

	return null;
}

// 打开新增窗口
function onAddClicked() {
	if (!dataAuth.addUser) {
		return false;
	}
	$('#_passwordTd').show();
	$('#_lastLoginIpTd').hide();
	$('#_lastLoginTimeTd').hide();
	$('#_createdTd').hide();
	$('#_modifiedTd').hide();
	$('#_statusTd').hide();
	$('#_validTimeBeginTd').hide();
	$('#_validTimeEndTd').hide();
	$('#existsRoleTd').hide();
	$('#_userName').textbox({
				readonly : false
			});
	$('#_password').textbox({
				readonly : false,
				required : true
			});
	$('#_realName').textbox({
				readonly : false
			});
	$('#_serialNumber').textbox({
				readonly : false
			});
	$('#_fixedLineTelephone').textbox({
				readonly : false
			});
	$('#_user_cellphoneName').textbox({
				readonly : false
			});
	$('#_email').textbox({
				readonly : false
			});
	$('#_lastLoginIp').textbox({
				readonly : false
			});
	$('#_lastLoginTime').textbox({
				readonly : false
			});
	$('#_created').textbox({
				readonly : false
			});
	$('#_modified').textbox({
				readonly : false
			});
	$('#_validTimeBegin').textbox({
				readonly : false
			});
	$('#_validTimeEnd').textbox({
				readonly : false
			});
	$('#_status').textbox({
				readonly : false
			});
	$('#roleForm').show();

	$('#dlg').dialog({
				buttons : [{
							text : '确认',
							iconCls : 'icon-ok',
							handler : onSaveClicked
						}, {
							text : '取消',
							iconCls : 'icon-cancel',
							handler : function() {
								$('#dlg').dialog('close');
							}
						}]
			});

	formFocus("_form", "_userName");
	queryRole();

	$('#dlg').dialog('open');
	$('#dlg').dialog('center');
	$('#_form').form('clear');
	$('#_form').form('resetValidation');
}

function onEditClicked(id) {
	if (!dataAuth.editUser) {
		return false;
	}
	var selected = null == id ? userGrid.datagrid("getSelected") : getRowById(id);
	if (null == selected) {
		$.messager.alert("警告", "请选中一条数据");
		return;
	}

	var index = userGrid.datagrid("getRowIndex", selected);
	userGrid.datagrid("selectRow", index);
	$('#_passwordTd').show();
	$('#_lastLoginIpTd').hide();
	$('#_lastLoginTimeTd').hide();
	$('#_createdTd').hide();
	$('#_modifiedTd').hide();
	$('#_statusTd').hide();
	$('#_validTimeBeginTd').hide();
	$('#_validTimeEndTd').hide();
	$('#existsRoleTd').hide();;
	$('#_userName').textbox({
				readonly : true
			});
	$('#_password').textbox({
				readonly : false,
				required : false
			});
	$('#_realName').textbox({
				readonly : false
			});
	$('#_serialNumber').textbox({
				readonly : false
			});
	$('#_fixedLineTelephone').textbox({
				readonly : false
			});
	$('#_user_cellphoneName').textbox({
				readonly : false
			});
	$('#_email').textbox({
				readonly : false
			});
	$('#_lastLoginIp').textbox({
				readonly : false
			});
	$('#_lastLoginTime').textbox({
				readonly : false
			});
	$('#_created').textbox({
				readonly : false
			});
	$('#_modified').textbox({
				readonly : false
			});
	$('#_validTimeBegin').textbox({
				readonly : false
			});
	$('#_validTimeEnd').textbox({
				readonly : false
			});
	$('#_status').textbox({
				readonly : false
			});

	$('#roleForm').show();

	$('#dlg').dialog({
				buttons : [{
							text : '确认',
							iconCls : 'icon-ok',
							handler : onSaveClicked
						}, {
							text : '取消',
							iconCls : 'icon-cancel',
							handler : function() {
								$('#dlg').dialog('close');
							}
						}]
			});

	var formData = $.extend({}, selected);
	$('#_department').combotree({
				readonly : false,
				validateOnCreate : false,
				onLoadSuccess : function(node, data) {
					$(selected.departments).each(function(index, item) {
								var targetNode = $('#_department').combotree('tree').tree('find', item.id);;
								$('#_department').combotree('tree').tree('check', targetNode.target);
							});
				}
			});

	formData = addKeyStartWith(getOriginalData(formData), "_");
	formData._password = "";
	$('#_form').form('load', formData);
	queryRole(selected.id);

	$('#dlg').dialog('open');
	$('#dlg').dialog('center');
	formFocus("_form", "_userName");
}

function onDblClickRow(index, row) {
	onEditClicked();
}

// 打开某一行的查看窗口
function onUserDetailClicked(id) {
	var selected = null == id ? userGrid.datagrid("getSelected") : getRowById(id);
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}

	$('#_passwordTd').hide();
	$('#_lastLoginIpTd').show();
	$('#_lastLoginTimeTd').show();
	$('#_createdTd').show();
	$('#_modifiedTd').show();
	$('#_statusTd').show();
	$('#_validTimeBeginTd').hide();
	$('#_validTimeEndTd').hide();
	$('#existsRoleTd').show();
	$('#_userName').textbox({
				readonly : true
			});
	$('#_password').textbox({
				readonly : true
			});
	$('#_realName').textbox({
				readonly : true
			});
	$('#_serialNumber').textbox({
				readonly : true
			});
	$('#_fixedLineTelephone').textbox({
				readonly : true
			});
	$('#_user_cellphoneName').textbox({
				readonly : true
			});
	$('#_email').textbox({
				readonly : true
			});
	$('#_lastLoginIp').textbox({
				readonly : true
			});
	$('#_lastLoginTime').textbox({
				readonly : true
			});
	$('#_created').textbox({
				readonly : true
			});
	$('#_modified').textbox({
				readonly : true
			});
	$('#_validTimeBegin').textbox({
				readonly : true
			});
	$('#_validTimeEnd').textbox({
				readonly : true
			});
	$('#_status').textbox({
				readonly : true
			});
	$('#_department').textbox({
				readonly : true
			});
	$('#roleForm').hide();

	$('#dlg').dialog({
				buttons : [{
							text : '关闭',
							iconCls : 'icon-cancel',
							handler : function() {
								$('#dlg').dialog('close');
							}
						}]
			});

	var formData = $.extend({}, selected);
	formData = addKeyStartWith(getOriginalData(formData), "_");
	var comboText = '';
	$(formData._departments).each(function(index, item) {
				comboText += item.name + ',';
			});

	comboText = comboText.substring(0, comboText.length - 1)
	$('#_department').combotree('setText', comboText);
	$('#_form').form('load', formData);
	loadData4DataList("existsRole", "${contextPath!}/role/listByUserId", {
				userid : selected.id
			});

	$('#dlg').dialog('open');
	$('#dlg').dialog('center');
	formFocus("_form", "_userName");
}

// 禁用/启用某一行的用户
function onChangeUserStatusClicked(id) {
	var selected = null == id ? userGrid.datagrid("getSelected") : getRowById(id);
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}

	var tips = null;
	var url = null;
	var status = selected.$_status;
	if (0 == status) {
		// 0 是已停用，功能对应是 启用
		tips = "启用";
		url = "${contextPath!}/user/enableUser";
	} else if (1 == status) {
		// 1 是已启用，功能对应是 禁用
		tips = "禁用";
		url = "${contextPath!}/user/disableUser";
	} else {
		$.messager.alert('警告', '未知的用户状态');
		return;
	}

	$.messager.confirm('确认', '您确认想要' + tips + '用户(' + selected.userName + ')吗？', function(r) {
				if (r) {
					var index = userGrid.datagrid("getRowIndex", selected);
					requestWithAjax(url, {
								id : selected.id
							}, function() {
								if (0 == status) {
									selected.$_status = 1;
									selected.status = "已启用";
								} else if (1 == status) {
									selected.$_status = 0;
									selected.status = "已停用";
								}

								$('#dlg').dialog('close');
								userGrid.datagrid("refreshRow", index);
							});
				}
			});
}

// 删除某一行的用户
function onRemoveClicked(id) {
	if (!dataAuth.removeUser) {
		return false;
	}
	var selected = null == id ? userGrid.datagrid("getSelected") : getRowById(id);
	if (null == selected) {
		$.messager.alert("警告", "请选中一条数据");
		return;
	}

	$.messager.confirm("确认", "您确认想要删除用户(" + selected.userName + ")吗？", function(r) {
				if (r) {
					var index = userGrid.datagrid("getRowIndex", selected);
					requestWithAjax("${contextPath!}/user/logicDelete", {
								id : selected.id
							}, function() {
								userGrid.datagrid("deleteRow", index);
							});
				}
			});
}

function onPageUpClicked() {
	var maxRowIndex = userGrid.datagrid("getRows").length - 1;
	var selected = userGrid.datagrid("getSelected");
	if (null == selected) {
		if (0 <= maxRowIndex) {
			userGrid.datagrid("selectRow", maxRowIndex);
		}
		// maxRownIndex 小于0 表示datagrid没有数据
		return;
	}

	var index = userGrid.datagrid("getRowIndex", selected) - 1;
	userGrid.datagrid("selectRow", index < 0 ? maxRowIndex : index);
}

function onPageDownClicked() {
	var maxRowIndex = userGrid.datagrid("getRows").length - 1;
	var selected = userGrid.datagrid("getSelected");
	if (null == selected) {
		if (0 <= maxRowIndex) {
			userGrid.datagrid("selectRow", 0);
		}
		// maxRownIndex 小于0 表示datagrid没有数据
		return;
	}

	var index = userGrid.datagrid("getRowIndex", selected) + 1;
	userGrid.datagrid("selectRow", index > maxRowIndex ? 0 : index);
}

function onSaveClicked() {
	if (!$('#_form').form("validate")) {
		return;
	}

	var _formData = removeKeyStartWith($("#_form").serializeObject(), "_");
	var nodes = $('#_department').combotree('tree').tree('getChecked', 'checked');
	_formData.department = new Array();
	$(nodes).each(function(index, item) {
				_formData.department.push(item.id)
			});
	var _url = null;

	var isAdd = false;
	if (_formData.id == null || _formData.id == "") {
		// 没有id就新增
		_url = "${contextPath!}/user/add";
		isAdd = true;
	} else {
		// 有id就修改
		_url = "${contextPath!}/user/update";
	}
	var roleList = [];
	var rows = $("#withRoleDatalist").datalist("getRows");

	$(rows).each(function(index, item) {
				roleList.push(item.id);
			});
	_formData.roleId = roleList;

	var temp = JSON.stringify(_formData);
	requestSave(_url, temp, function(retData) {
				if (null != retData) {
					if (isAdd) {
						userGrid.datagrid("appendRow", retData);
					} else {
						var index = userGrid.datagrid("getRowIndex", userGrid.datagrid("getSelected"));
						userGrid.datagrid("updateRow", {
									index : index,
									row : retData
								}).datagrid("refreshRow", index);
					}
				}
				$('#dlg').dialog('close');
			});
}

// --------------------------------------------------------------------------------
function requestWithAjax(url, data, callback) {
	$.ajax({
				type : "POST",
				url : url,
				data : data,
				dataType : "json",
				processData : true,
				async : true,
				success : function(retData) {
					if (retData.code == "200") {
						(callback && typeof(callback) === "function") && callback(retData.data);
					} else {
						$.messager.alert('错误', retData.result);
					}
				},
				error : function() {
					$.messager.alert('错误', '远程访问失败');
				}
			});
}

function requestSave(url, data, callback) {
	$.ajax({
				type : "POST",
				url : url,
				data : data,
				contentType : 'application/json',
				dataType : "json",
				async : true,
				success : function(retData) {
					if ("200" == retData.code) {
						(callback && typeof(callback) === "function") && callback(retData.data);
					} else {
						$.messager.alert('错误', retData.result);
					}
				},
				error : function() {
					$.messager.alert('错误', '远程访问失败');
				}
			});
}

// 角色信息查询
function queryRole(userid) {
	if (null == userid || 0 > userid) {
		loadData4DataList("withoutRoleDatalist", "${contextPath!}/role/list");
		$('#withRoleDatalist').datalist("loadData", {
					total : 0,
					rows : []
				});
	} else {
		loadData4DataList("withoutRoleDatalist", "${contextPath!}/role/listNotBindByUserId", {
					userid : userid
				});
		loadData4DataList("withRoleDatalist", "${contextPath!}/role/listByUserId", {
					userid : userid
				});
	}
}

// 表格查询
function queryGrid() {
	var opts = userGrid.datagrid("options");
	if (null == opts.url || "" == opts.url) {
		opts.url = "${contextPath}/user/listPage";
	}
	if (!$('#userForm').form("validate")) {
		return;
	}

	var param = bindMetadata("userGrid", true);
	var formData = $("#userForm").serializeObject();
	$.extend(formData, param);
	userGrid.datagrid("load", formData);
}

// --------------------------------------------------------------------------------
// 表格表头右键菜单
function headerContextMenu(e, field) {
	e.preventDefault();
	if (!cmenu) {
		createColumnMenu("userGrid");
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
	if (46 == keycode) {
		// DELETE
		onRemoveClicked();
	} else if (38 == keycode) {
		// PageUp
		onPageUpClicked();
	} else if (40 == keycode) {
		// PageDown
		onPageDownClicked();
	} else if (45 == keycode) {
		// INSERT
		onEditClicked();
	}
}

$(function() {
			bindFormEvent("userForm", "userName", queryGrid);
			bindFormEvent("_form", "_userName", onSaveClicked, function() {
						$('#dlg').dialog('close');
					});
			if (document.addEventListener) {
				document.addEventListener("keyup", getKey, false);
			} else if (document.attachEvent) {
				document.attachEvent("onkeyup", getKey);
			} else {
				document.onkeyup = getKey;
			}

			window.userGrid = $('#userGrid');
			queryGrid();
		});

openDataAuth = function(userId) {
	if (!dataAuth.viewUserDataAuth) {
		return false;
	}
	$('#win').window({
				title : '编辑数据权限',
				minimizable : false,
				maximizable : false,
				width : 900,
				height : 500,
				href : '${contextPath}/dataAuth/editUserDataAuth.html?userId=' + userId,
				onLoad : function() {
					window.userId = $('#userId').val();
					window.dataAuthType = $('#dataAuthType').val();
					loadTree(userId, dataAuthType);
				}
			});
};

loadTree = function(userId, type) {
	$.get('${contextPath!}/dataAuth/editUserDataAuth.json', {
				userId : userId,
				type : type
			}, function(data) {
				if (data.code != 200) {
					$.messager.alert('提示', data.result);
				}
				var znodes = data.data.allDataAuths;
				var userDataAuths = data.data.userDataAuths;
				$.each(znodes, function(index, node) {
							$.each(userDataAuths, function(i, n) {
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

saveUserDataAuth = function() {
	var tree = $.fn.zTree.getZTreeObj("tree");
	var checkedNodes = tree.getCheckedNodes();
	var submitData = {
		userId : userId,
		type : dataAuthType,
		dataAuthIds : []

	};
	$.each(checkedNodes, function(index, node) {
				submitData.dataAuthIds.push(node.id);
			});
	$.ajax({
				type : "POST",
				url : '${contextPath!}/dataAuth/updateUserDataAuth.json',
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
	loadTree(userId, newVal);
};