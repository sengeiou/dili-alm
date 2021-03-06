var userId = '${user.id!}';
userId = parseInt(userId);

var approveType = {
	projectManagerApprove : 'managerApprove',
	generalManagerApprove : 'generalManagerApprove',
	operationManagerApprove : 'operationManagerApprove'
};

function deptMemberFormatter(value, row, index) {
	return row.applicationDepartmentId + '/' + row.applicantId;
}

function serialNumberFormatter(value, row, index) {
	return '<a href="javascript:void(0);" onclick="showDetail(' + row.id
			+ ');">' + value + '</a>';
}

function showDetail(id) {
	$('#win').dialog({
				title : '资源申请详情',
				width : 830,
				height : 500,
				href : '${contextPath!}/hardwareResourceApply/detail?id=' + id,
				modal : true,
				buttons : [{
							text : '返回',
							handler : function() {
								$('#win').dialog('close');
							}
						}]
			});
}

function opptFormatter(value, row, index) {
	var returnStr = "";
	if (row.approving) {
		returnStr = "<a href='javascr:void(0)' onclick='openUpdate(" + index
				+ ")'>编辑</a>&nbsp<a href='javascr:void(0)' onclick='del("
				+ index + ");'>删除</a>";
	} else if (row.$_applyState == "1") {
		returnStr = "<span> 编辑 &nbsp; 删除 </span>";
	}

	if (row.projectManagerApprov) {
		returnStr = '<span><a href="javascr:void(0);" onclick="openApove('
				+ row.id + ',\'' + approveType.projectManagerApprove
				+ '\');">审批</a></span>';
	} else if (row.$_applyState == "2") {
		returnStr = "<span> 审批  </span>";
	}

	if (row.generalManagerAppov) {
		returnStr = '<span><a href="javascr:void(0);" onclick="openApove('
				+ row.id + ',\'' + approveType.generalManagerApprove
				+ '\');">审批</a></span>';
	} else if (row.$_applyState == "3") {
		returnStr = "<span> 审批  </span>";
	}

	if (row.operactionManagerAppov) {
		returnStr = '<span><a href="javascr:void(0);" onclick="openApoveForOp('
				+ row.id + ',\'' + approveType.operationManagerApprove
				+ '\');">分配实施 </a></span>';
	} else if (row.$_applyState == "4") {
		returnStr = "<span> 分配实施  </span>";
	}

	if (row.implementing) {
		returnStr = "<span><a href='javascr:void(0)' onclick='implement("
				+ row.id + ")'>实施</a></span>";
	} else if (row.$_applyState == "5") {
		returnStr = "<span> 实施  </span>";
	}

	if (row.$_applyState == 6 || row.$_applyState == -1) {
		returnStr = "<span><a href='javascr:void(0)' onclick='showDetail("
				+ row.id + ")'>查看</a></span>";
	}
	return returnStr;
}

function envFormatter(v, r, i) {
	var content = '';
	$(v).each(function(index, item) {
				content += item + ',';
			});
	content = content ? content.substring(0, content.length - 1) : '';
	return content;
}

// 初始化
function loadProject() {
	var data = $('#projectId').combobox('getValue');
}
// 项目框有变
function changeProject() {
	var id = $('#projectId').combobox('getValue');
	changeProjectSetValue(id);
}

function changeProjectSetValue(id) {
	$.post('${contextPath!}/project/listViewData.json', {
				id : id
			}, function(res) {
				if (res && res.length > 0) {
					var p = res[0];
					$('#projectManager').textbox('initValue', p.projectManager);
					$('#projectSerialNumber').textbox('initValue',
							p.serialNumber);
					$('#projectName-a').val(p.name);
					$('#projectManagerId').val(p.$_projectManager);
				}
			}, 'json');
}
// 打开新增窗口
function openInsert() {
	if (!dataAuth.addHardwareResourceApply && userid == 1) {
		return false;
	}

	$('#win').dialog({
		title : '申请资源',
		width : 830,
		height : 500,
		href : '${contextPath!}/hardwareResourceApply/add',
		modal : true,
		buttons : [{
					text : '保存',
					handler : function() {
						var data = $("#editForm").serializeArray();
						var data2 = createConfigurationRequirementJson();
						if (data2 == "") {
							$.messager.alert('错误', "配置信息尚未添加！");
							return;
						}
						$('#editForm').form('submit', {
									url : '${contextPath!}/hardwareResourceApply/save',
									queryParams : data2,
									success : function(data) {
										var obj = $.parseJSON(data);
										if (obj.code == 200) {
											$('#grid').datagrid('reload');
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
				}/**, {
					text : '提交',
					handler : function() {
						var data = $("#editForm").serializeArray();
						var data2 = createConfigurationRequirementJson();
						if (data2 == "") {
							$.messager.alert('错误', "配置信息尚未添加！");
							return;
						}
						$('#editForm').form('submit', {
							url : '${contextPath!}/hardwareResourceApply/saveAndSubmit',
							queryParams : data2,
							success : function(data) {
								var obj = $.parseJSON(data);
								if (obj.code == 200) {
									$('#grid').datagrid('reload');
									$('#win').dialog('close');
								} else {
									$.messager.alert('错误', obj.result);
								}
							}
						});
					}
				}**/]
	});

}

// 打开修改窗口
function openUpdate(index) {
	if (!dataAuth.addHardwareResourceApply || userId == 1) {
		return false;
	}
	var selected = null;
	if (!index) {
		selected = $("#grid").datagrid("getSelected");
	} else {
		selected = $('#grid').datagrid('getRows')[index];
	}
	if (!selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	if (selected.$_applyState != 1) {
		return false;
	}
	console.log(selected);
	$('#win').dialog({
		title : '资源申请详情',
		width : 830,
		height : 500,
		href : '${contextPath!}/hardwareResourceApply/toUpdate?id='+ selected.id,
		modal : true,
		buttons : [{
					text : '保存',
					handler : updateForms
				}, {
					text : '关闭',
					handler : function() {
						$('#win').dialog('close');
					}
				}, {
					text : '提交',
					handler : function() {
					   
					    if(jQuery.isEmptyObject($("#submitBtn"))){
					    $("#submitBtn").linkbutton("disable");
					    }
						$.post('${contextPath!}/hardwareResourceApply/submit',
								{
									id : selected.id
								}, function(res) {
									if (res.success) {
									   $.messager.alert('提示', res.result);
										$('#grid').datagrid('reload');
										$('#win').dialog('close');
									} else {
										$.messager.alert('错误', res.result);
									}
								});
					}
				}]
	});

}

// 申请审批窗口
function openApove(id, type) {
	var selected = $("#grid").datagrid("getSelected");
	if (id == null) {
		if (null == selected) {
			$.messager.alert('警告', '请选中一条数据');
			return;
		} else {
			id = selected.id;

		}
	}
	var rows = $("#grid").datagrid('getRows');

	var length = rows.length;
	var rowindex;
	for (var i = 0; i < length; i++) {
		if (rows[i]['id'] == id) {
			rowindex = i;
			selected = rows[rowindex];// 根据index获得其中一行。
			break;
		}
	}

	$('#win').dialog({
		title : '资源申请详情',
		width : 830,
		height : 500,
		href : '${contextPath!}/hardwareResourceApply/goApprove?id='
				+ selected.id,
		modal : true,
		buttons : [{
					text : '审批通过',
					handler : function() {
						approve();
					}
				}, {
					text : '不通过',
					handler : function() {
						noAppov();
					}
				}, {
					text : '关闭',
					handler : function() {
						$('#win').dialog('close');
					}
				}]
	});

}

// 分配发布
function openApoveForOp(id, type) {
	var selected = $("#grid").datagrid("getSelected");
	if (id == null) {
		if (null == selected) {
			$.messager.alert('警告', '请选中一条数据');
			return;
		} else {
			id = selected.id;

		}
	}
	var rows = $("#grid").datagrid('getRows');

	var length = rows.length;
	var rowindex;
	for (var i = 0; i < length; i++) {
		if (rows[i]['id'] == id) {
			rowindex = i;
			selected = rows[rowindex];// 根据index获得其中一行。
			break;
		}
	}

	$('#win').dialog({
		title : '资源申请详情',
		width : 830,
		height : 500,
		href : '${contextPath!}/hardwareResourceApply/goApprove?id='
				+ selected.id,
		modal : true,
		buttons : [{
					text : '分配',
					handler : function() {
						approve();
					}
				}, {
					text : '关闭',
					handler : function() {
						$('#win').dialog('close');
					}
				}]
	});

}
function checkExecutorAmount(nval, oval) {
	var me = $(this);
	if (nval.length > 2) {
		me.combobox('unselect', nval[nval.length - 1]);
	}
}

// 实施窗口
function implement(id) {
	$('#win').dialog({
				title : '资源申请详情',
				width : 830,
				height : 500,
				href : '${contextPath!}/hardwareResourceApply/goApprove?id='
						+ id,
				modal : true,
				buttons : [{
							text : '完成',
							handler : function() {
								approve();
							}
						}, {
							text : '关闭',
							handler : function() {
								$('#win').dialog('close');
							}
						}]
			});

}

function updateForms() {
	var data = $("#editForm2").serializeArray();
	var data2 = createConfigurationRequirementJson();
	if (data2 == "") {
		$.messager.alert('错误', "配置信息尚未添加！");
		return;
	}
	$('#editForm2').form('submit', {
		url : '${contextPath!}/hardwareResourceApply/update',
		queryParams : data2,
		success : function(data) {
			var obj = $.parseJSON(data);
			if (obj.code == 200) {

				$('#grid').datagrid('reload');
				$('#grid').datagrid('updateRow', {
					index : $('#grid').datagrid('getRowIndex',
							$('#grid').datagrid('getSelected')),
					row : obj.data
				});
				$('#win').dialog('close');
			} else {
				$.messager.alert('错误', obj.result);
			}
		}
	});
}

function saveOrUpdate() {
	if (!$('#_form').form("validate")) {
		return;
	}
	var _formData = removeKeyStartWith($("#_form").serializeObject(), "_");
	var _url = null;
	// 没有id就新增
	if (_formData.id == null || _formData.id == "") {
		_url = "${contextPath}/hardwareResourceApply/insert";
	} else {// 有id就修改
		_url = "${contextPath}/hardwareResourceApply/update";
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
function del(index) {
	if (!dataAuth.deleteHardwareResourceApply || userId == 1) {
		return false;
	}
	var selected = null;
	if (index == undefined) {
		selected = $("#grid").datagrid("getSelected");
	} else {
		selected = $('#grid').datagrid('getRows')[index];
	}
	if (!selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	if (selected.$_applyState != 1) {
		return false;
	}
	$.messager.confirm('确认', '您确认想要删除记录吗？', function(r) {
				if (r) {
					$.ajax({
								type : "POST",
								url : "${contextPath}/hardwareResourceApply/delete",
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
		opts.url = "${contextPath}/hardwareResourceApply/listPage";
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
	$('#applicantId').textbox('setText', '');
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
			queryGrid();
			if (document.addEventListener) {
				document.addEventListener("keyup", getKey, false);
			} else if (document.attachEvent) {
				document.attachEvent("onkeyup", getKey);
			} else {
				document.onkeyup = getKey;
			}
		});

/** *添加配置要求*** */
function add() {
	if (!$('#configForm').form('validate')) {
		return;
	}
	var rows = $('#configGrid').datagrid('getRows');

	var formInput = $('#configForm').serializeArray();
	var formData = $.extend({}, formInput);

	var map = {};
	$.each(formData, function(i, item) {
				if (item.name == 'cpuAmount') {
					var key = 'cpuAmount';
					map[key] = item.value;
				}
				if (item.name == 'diskAmount') {
					var key = 'diskAmount';
					map[key] = item.value;
				}
				if (item.name == 'memoryAmount') {
					var key = 'memoryAmount';
					map[key] = item.value;
				}
				if (item.name == 'notes') {
					var key = 'notes';
					map[key] = item.value;
				}
			});
	if ($("#updateSige").val()) {
		$('#configGrid').datagrid('updateRow', {
					index : $("#updateSige").val(),
					row : map
				});
	} else {
		$('#configGrid').datagrid('appendRow', map);
	}

	$('#configForm').form('clear');
	$('#addConfig').dialog('close');
}

function showAddConfigDialog() {
	$('#configForm').form('clear');
	$('#addConfig').dialog('open');
}

// 保存列表配置
function delConfigGrid(index) {
	$('#configGrid').datagrid('deleteRow', index)
}
function update(rowId) {
	var row = $('#configGrid').datagrid('getRows')[rowId];
	var formData = $.extend({}, row);
	$('#configForm').form('load', formData);
	$('#addConfig').dialog('open');
	$('#updateSige').val(rowId);
}
function optionFormatter(value, row, index) {
	return "<a href='JavaScript:delConfigGrid(" + index
			+ ")'>删除</a>&nbsp;&nbsp;<a href='JavaScript:update(" + index
			+ ")'>修改</a>";
}

// 配置要求的json串
function createConfigurationRequirementJson() {
	var objList = [];
	var rowobj = $('#configGrid').datagrid('getRows');
	if (rowobj.length < 1) {
		return "";
	}
	for (var index = 0; index < rowobj.length; index++) {
		var obj = {};
		obj.memoryAmount = rowobj[index].memoryAmount;
		obj.cpuAmount = rowobj[index].cpuAmount;
		obj.diskAmount = rowobj[index].diskAmount;
		obj.notes = rowobj[index].notes;
		objList.push(obj);

	}
	var _formData = {
		"configurationRequirementJsonStr" : JSON.stringify(objList)
	};
	return _formData;
}

$.extend($.fn.dialog.methods, {
			addButtonsAItem : function(jq, items) {
				return jq.each(function() {
							$('win-button').html();
						});
			},
			addButtonsBItem : function(jq, param) {
				return jq.each(function() {
							$('win-button2').html();
						});
			}
		});
