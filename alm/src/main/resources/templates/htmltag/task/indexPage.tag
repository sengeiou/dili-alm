var userId = '${user.id!}';
userId = parseInt(userId);
// 打开新增窗口
function openInsert() {
	canEdit();
	// defaultValue();
	$("#task_detail").hide();
	$("#dialog_toolbar").show();
	$("#saveTask").show();
	$("#dialog_toolbar_detail").hide();
	$("#task_detail_list").hide();
	$('#dlg').dialog('open');
	$('#dlg').dialog('center');
	$('#_form').form('clear');
	if ($('#flowSt').combobox('getValue') == "") {
		$('#flowSt').combobox('select', 1)
	}
	formFocus("_form", "_name");
}
// 打开修改窗口
function openUpdate() {
	var selected = $("#grid").datagrid("getSelected");
	if (!selected.canOperation) {
		$.messager.alert('提示', '项目或版本暂停中，不能删除任务');
		return;
	}

	$("#task_detail").hide();
	$("#dialog_toolbar").show();
	$("#saveTask").show();
	$("#dialog_toolbar_detail").hide();
	$("#task_detail_list").hide();

	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}

	canEdit();

	if (!isCreater(selected.id) || !selected.updateDetail) {
		noEdit();
	}

	if (selected.$_status != 0) {
		noEdit();
	}

	$('#dlg').dialog('open');
	$('#dlg').dialog('center');
	formFocus("_form", "_name");
	var formData = $.extend({}, selected);
	formData = addKeyStartWith(getOriginalData(formData), "_");
	$('#_form').form('load', formData);
	$('#_form').form('load', {
				planTimeStr : formData._planTime
			});

	loadMemberSelect(formData._projectId);
	$('#_owner').combobox('select', formData._owner);
	loadVisionSelect(formData._projectId);
	$('#_versionId').combobox('select', formData._versionId);
	loadTaskSelect(formData._projectId);
	$('#_beforeTask').combobox('select', formData._beforeTask);
	$('#_form').form('load', {
				startDateShow : dateFormat_1(formData._startDate)
			});
	$('#_form').form('load', {
				endDateShow : dateFormat_1(formData._endDate)
			});

	if (selected.flow) {
		$("#changeIdTr").css("display", "block");
		loadChangePorjectSelect();
		$('#_changeId').combobox('select', formData._changeId);
		$('#flowSt').combobox('select', 0);
	} else {
		$("#changeIdTr").css("display", "none");
		$('#flowSt').combobox('select', 1);
	}
}

function copyTask(row) {
	loadProjectSelect();
	$("#task_detail").hide();
	$("#dialog_toolbar").show();
	$("#saveTask").show();
	$("#dialog_toolbar_detail").hide();
	$("#task_detail_list").hide();

	var selected = $("#grid").datagrid('getData').rows[row];

	$('#dlg').dialog('open');
	$('#dlg').dialog('center');

	var formData = $.extend({}, selected);
	formData = addKeyStartWith(getOriginalData(formData), "_");

	canEdit();

	$('#_form').form('load', formData);
	$('#_form').form('load', {
				planTimeStr : formData._planTime
			});

	if (selected.flow) {
		$("#changeIdTr").css("display", "block");
		loadChangePorjectSelect(formData._projectId);
		$('#_changeId').combobox('select', formData._changeId);
		$('#flowSt').combobox('select', 0);
	} else {
		$("#changeIdTr").css("display", "none");
		$('#flowSt').combobox('select', 1);
	}

	loadMemberSelect(formData._projectId);
	$('#_owner').combobox('select', formData._owner);

	loadVisionSelect(formData._projectId);
	$('#_versionId').combobox('select', formData._versionId);

	loadTaskSelect(formData._projectId);
	$('#_beforeTask').combobox('select', formData._beforeTask);

	$('#_form').form('load', {
				startDateShow : dateFormat_1(formData._startDate)
			});
	$('#_form').form('load', {
				endDateShow : dateFormat_1(formData._endDate)
			});
	$('#_form').form('load', {
				_id : formData.id
			});// 清除ID
}

function saveOrUpdate() {
	var logInfo = "";
	var moduleInfo = "";
	$("#task_detail").hide();
	if (!$('#_form').form("validate")) {
		return;
	}

	var _formData = removeKeyStartWith($("#_form").serializeObject(), "_");
	var _url = null;

	// 没有id就新增
	if (_formData.id == null || _formData.id == "") {
		logIngo = "新增任务:";
		moduleInfo = LOG_MODULE_OPS.ADD_PROJECT_VERSION_PHASE_TASK;
		_url = "${contextPath}/task/insert";
	} else { // 有id就修改
		logIngo = "修改任务:";
		moduleInfo = LOG_MODULE_OPS.UPDATE_PROJECT_VERSION_PHASE_TASK;
		_url = "${contextPath}/task/update";
	}
	if (isProjectDate()) {
		$.messager.alert('错误', "不符合项目开始时间和结束时间");
		return;
	}
	$.ajax({
				type : "POST",
				url : _url,
				data : _formData,
				processData : true,
				dataType : "json",
				async : true,
				beforeSend : function(xhr) {
					$.messager.progress({
								title : '提示',
								msg : '数据处理中，请稍候……',
								text : ''
							});
				},
				success : function(data) {
					$.messager.progress('close');
					if (data.code == "200") {
						$("#grid").datagrid("reload");
						LogUtils.saveLog(moduleInfo, logIngo + data.data + ":成功", function() {
								});
						$('#dlg').dialog('close');
					} else {
						$.messager.alert('错误', data.result);
					}
				},
				error : function() {
					$.messager.progress('close');
					$.messager.alert('错误', '远程访问失败');
				}
			});
}

// 根据主键删除
function del() {
	var selected = $("#grid").datagrid("getSelected");

	if (!selected.canOperation) {
		$.messager.alert('提示', '项目或版本暂停中，不能删除任务');
		return;
	}

	if (!isCreater(selected.id)) {
		$.messager.alert('警告', '并非任务创建者,不能执行删除操作');
		return;
	}

	if (!selected.updateDetail) {
		debugger;
		$.messager.alert('警告', '当前任务状态不能执行删除操作');
		return;
	}

	if (selected.$_status != 0) {
		$.messager.alert('警告', '不是未开始的任务不能删除！');
		return;
	}
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	$.messager.confirm('确认', '您确认想要删除记录吗？', function(r) {
				if (r) {
					$.ajax({
								type : "POST",
								url : "${contextPath}/task/delete",
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
										LogUtils.saveLog(LOG_MODULE_OPS.DELETE_PROJECT_VERSION_PHASE_TASK, "删除任务:" + data.data + ":成功", function() {
												});
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
function queryGrid(obj) {
	if (obj == 1) {
		$("#taskId").val(null);
	}
	var opts = $("#grid").datagrid("options");
	if (null == opts.url || "" == opts.url) {
		opts.url = "${contextPath}/task/listTeamPage";
	}
	if (!$('#form').form("validate")) {
		return;
	}
	var param = bindMetadata("grid", true);
	var formData = $("#form").serializeObject();
	$.extend(param, formData);
	$("#grid").datagrid("load", param);
	$('#detail_form').form('load', {
				_taskHour : formData._planTime
			});
}

// 清空表单
function clearForm() {
	$('#form').form('clear');
	// $("#owner").com('initValue', '');
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
			bindFormEvent("form", "name", queryGrid);
			if (document.addEventListener) {
				document.addEventListener("keyup", getKey, false);
			} else if (document.attachEvent) {
				document.attachEvent("onkeyup", getKey);
			} else {
				document.onkeyup = getKey;
			}
			queryGrid();
		})

function formatOptions(value, row, index) {
	var content = '';
	if (row.updateDetail && row.canOperation) {
		content += '<span style="padding:2px;"><a href="javascript:void(0)" onclick="openUpdateDetail(' + index + ')">执行任务</a></span>';
	} else {
		content += '<span style="padding:2px;color:#8B8B7A;text-decoration:underline;">执行任务</span>';
	}
	if (row.copyButton && row.canOperation) {
		content += '<span style="padding:2px;"><a href="javascript:void(0)" onclick="copyTask(' + index + ')">复制</a></span>';
	}
	if (row.$_status == 1 || row.$_status == 4) {
		if (row.$_owner == userId || row.projectManagerId == userId) {
			content += '<span style="padding:2px;"><a href="javascript:void(0)" onclick="complate(' + row.id + ')">完成任务</a></span>';
		} else {
			content += '<span style="padding:2px;color:#8B8B7A;text-decoration:underline;">完成任务</span>';
		}
	}
	return content;
}
// 进度条处理
function progressFormatter(value, rowData, rowIndex) {
	var progress = rowData.progress;
	if (rowData.progress > 100) {
		progress = 100;
	}
	var htmlstr = '<div style="width: 100px; height:20px;border: 1px solid #299a58;"><div style="width:' + progress + 'px; height:20px; background-color: #299a58;"><span>' + rowData.progress
			+ '%</span></div></div>';
	return htmlstr;
}

function getDetailInfo(taskID) {

	var htmlobj = $.ajax({
				url : "${contextPath}/task/listTaskDetail.json?id=" + taskID,
				async : false
			});
	var str = htmlobj.responseText;
	var obj = $.parseJSON(str);

	$('#detail_form').form('load', obj);
	$('#detail_form').form('load', {
				_taskHour : obj.taskHour,
				_overHour : obj.overHour
			});
}

function formatNameOptions(value, row, index) {
	var content = '<span style="padding:2px;"><a href="javascript:void(0)" onclick="openDetail(' + index + ')">' + row.name + '</a></span>';
	return content;
}

function ownerFormatter(value, row, index) {
	var content = $("#_owner").combobox('getText');
	return content;
}

function startTask() {
	$('#dlg').dialog('close');
	$.messager.progress({
				title : '提示',
				msg : '数据处理中，请稍候……',
				text : ''
			});
	var id = $("#_id").val();
	$.ajax({
				type : "POST",
				url : "${contextPath}/task/updateTaskStatus?id=" + id,
				processData : true,
				dataType : "json",
				async : true,
				success : function(data) {
					$.messager.progress('close');
					if (data.code == "200") {
						$("#grid").datagrid("reload");
						$.messager.alert('信息', '任务开始成功');
						LogUtils.saveLog(LOG_MODULE_OPS.START_PROJECT_VERSION_PHASE_TASK, "开始任务:" + data.data + ":成功", function() {
								});
					} else {
						$.messager.alert('错误', data.result);
					}
				},
				error : function() {
					$.messager.progress('close');
					$.messager.alert('错误', '远程访问失败');
				}
			});

}

function pauseTaskStatus() {
	$('#dlg').dialog('close');
	var id = $("#_id").val();
	$.ajax({
				type : "POST",
				url : "${contextPath}/task/pauseTaskStatus?id=" + id,
				processData : true,
				dataType : "json",
				async : true,
				success : function(data) {
					if (data.code == "200") {
						$("#grid").datagrid("reload");
						LogUtils.saveLog(LOG_MODULE_OPS.PAUSE_PROJECT_VERSION_PHASE_TASK, "暂停任务:" + data.data + ":成功", function() {
								});
					} else {
						$.messager.alert('错误', data.result);
					}
				},
				error : function() {
					$.messager.alert('错误', '远程访问失败');
				}
			});

}
$.extend($.fn.validatebox.defaults.rules, {
			endDateValidate : {
				validator : function(value, param) {
					var s = $("input[name=" + param[0] + "]").val();
					return value >= s;
				},
				message : '结束时间必须大于开始时间！'
			}
		});