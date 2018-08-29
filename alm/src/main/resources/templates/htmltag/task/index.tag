$(document).ready(function() {

			$('#modified').datebox('calendar').calendar({
						validator : function(date) {
							var now = new Date();
							return now.getTime() / 1000 >= date.getTime() / 1000;
						}
					});

			$('#owner').textbox('addClearBtn', 'icon-clear');

			loadProjectSelect();
			loadVisionCondition();
			loadProjectCondition();

			$("#changeIdTr").css("display", "none");
			$('#_projectId').combobox({
						onChange : function(o, n) {
							// 获取到的项目ID
							var projectId = $('#_projectId').combobox('getValue');
							if (isDefaultValue(projectId)) {
								return;
							}
							loadVisionSelect(projectId);
							loadMemberSelect(projectId);
							loadTaskSelect(projectId);
							loadChangePorjectSelect(projectId);
						}
					});

			$('#flowSt').combobox({
						onSelect : function(o, n) {
							// 获取到的项目ID
							if (o.value == 0) {
								$("#changeIdTr").css("display", "block");
							} else {
								$("#changeIdTr").css("display", "none");
								loadChangePorjectSelect($('#_projectId').combobox('getValue'));
							}
						}
					});
		});

function isProjectDate() {

	// 获取到的项目ID
	var projectId = $('#_projectId').combobox('getValue');
	var stratDate = $('#startDateShow').val();
	var endDate = $('#endDateShow').val();
	var htmlobj = $.ajax({
				url : "${contextPath}/task/isProjectDate.json?projectId=" + projectId + "&startDateShow=" + stratDate + "&endDateShow=" + endDate,
				async : false
			});
	var str = htmlobj.responseText;
	var obj = $.parseJSON(str);
	return obj;

}
function loadProjectSelect(queryAll) {
	debugger;
	$('#_projectId').combobox({
				url : "${contextPath!}/task/listTreeProject.json" + (queryAll ? '?queryAll=true' : ''),
				valueField : 'id',
				textField : 'name',
				editable : false,
				onChange : function(o, n) {
					var versionId = $('#_versionId').combobox('getValue');
					loadPhaseSelect(versionId);
				}
			});

}

function loadVisionSelect(id) {
	$('#_versionId').combobox({
				url : "${contextPath!}/task/listTreeVersion.json?id=" + id,
				valueField : 'id',
				textField : 'version',
				editable : false,
				onChange : function(o, n) {
					var versionId = $('#_versionId').combobox('getValue');
					loadPhaseSelect(versionId);
				}
			});

}
function loadMemberSelect(id) {
	$('#_owner').combobox({
				url : "${contextPath!}/task/listTreeUserByProject.json?projectId=" + id,
				valueField : 'id',
				textField : 'realName',
				editable : false,
				onChange : function(o, n) {

				}
			});
}

function loadVisionCondition() {
	$('#versionId').combobox({
				url : "${contextPath!}/task/listTreeVersionByTeam.json",
				valueField : 'id',
				textField : 'version',
				editable : false,
				onChange : function(o, n) {

				}
			});
}

function loadProjectCondition() {
	$('#projectId').combobox({
				url : "${contextPath!}/task/listTreeProject.json",
				valueField : 'id',
				textField : 'name',
				editable : false,
				onChange : function(o, n) {

				}
			});
}
function loadPhaseSelect(id, selectId) {
	$('#_phaseId').combobox({
				url : "${contextPath!}/task/listTreePhase.json?id=" + id,
				valueField : 'id',
				textField : 'name',
				editable : false
			});
}

function loadTaskSelect(id) {
	$('#_beforeTask').combobox({
				url : "${contextPath!}/task/listTree.json?projectId=" + id,
				valueField : 'id',
				textField : 'name',
				editable : false,
				onChange : function(o, n) {

				}
			});
}

function loadChangePorjectSelect(id) {
	$('#_changeId').combobox({
				url : "${contextPath!}/task/listTreeProjectChange.json?projectId=" + id,
				valueField : 'id',
				textField : 'name',
				editable : false,
				onChange : function(o, n) {

				}
			});
}

function ownerFormatter(value, row, index) {
	var content = $("#_owner").combobox('getText');
	return content;
}

function noEdit() {
	$('#_name').textbox('textbox').attr('readonly', true);
	$('#_describe').textbox('textbox').attr('readonly', true);
	$('#planTimeStr').textbox('textbox').attr('readonly', true);

	$("#_beforeTask").combobox({
				disabled : true
			});
	$("#_projectId").combobox({
				disabled : true
			});
	$("#_versionId").combobox({
				disabled : true
			});
	$("#_phaseId").combobox({
				disabled : true
			});
	$("#startDateShow").datebox({
				disabled : true
			});
	$("#endDateShow").datebox({
				disabled : true
			});
	$("#_owner").combobox({
				disabled : true
			});
	$("#flowSt").combobox({
				disabled : true
			});
	$("#_type").combobox({
				disabled : true
			});
	$("#_changeId").combobox({
				disabled : true
			});
	$("#saveTask").linkbutton({
				disabled : true
			});
}

function canEditForTaskdetail() {
	$('#taskHourStr').textbox('textbox').attr('readonly', false);
	$('#overHourStr').textbox('textbox').attr('readonly', false);
	$('#describe').textbox('textbox').attr('readonly', false);
	$('#doTask').linkbutton({
				disabled : false
			});
	$('#pauseTask').linkbutton({
				disabled : false
			});
	$('#updateDetail').linkbutton({
				disabled : false
			});
	$('#complate').linkbutton({
				disabled : false
			});
}

function canEdit() {

	$('#_name').textbox('textbox').attr('readonly', false);
	$('#_describe').textbox('textbox').attr('readonly', false);
	$('#planTimeStr').textbox('textbox').attr('readonly', false);

	$("#_beforeTask").combobox({
				disabled : false
			});
	$("#_projectId").combobox({
				disabled : false
			});
	$("#_versionId").combobox({
				disabled : false
			});
	$("#_phaseId").combobox({
				disabled : false
			});
	$("#startDateShow").datebox({
				disabled : false
			});
	$("#endDateShow").datebox({
				disabled : false
			});
	$("#_owner").combobox({
				disabled : false
			});
	$("#flowSt").combobox({
				disabled : false
			});
	$("#_type").combobox({
				disabled : false
			});
	$("#_changeId").combobox({
				disabled : false
			});
	$("#saveTask").linkbutton({
				disabled : false
			});

}

function noEditForTaskdetail() {
	$('#taskHourStr').numberbox('textbox').attr('readonly', true);
	$('#overHourStr').numberbox('textbox').attr('readonly', true);
	$('#describe').textbox('textbox').attr('readonly', true);
	$('#doTask').linkbutton({
				disabled : true
			});
	$('#pauseTask').linkbutton({
				disabled : true
			});
	$('#updateDetail').linkbutton({
				disabled : true
			});
	$('#complate').linkbutton({
				disabled : true
			});
	$("#modified").datebox({
				disabled : true
			});
}

// 显示详情页面
function openDetail(selected) {
	if (typeof selected == 'number') {
		$("#grid").datagrid("selectRow", selected);
		selected = $("#grid").datagrid("getSelected");
	}
	loadProjectSelect(true);
	noEdit();

	$("#task_detail_list").show();// 填写工时列表
	$("#dialog_toolbar").show();

	$("#saveTask").hide();
	$("#task_detail").hide();
	$("#dialog_toolbar_detail").hide();

	/** *****加载显示数据 begin********* */
	$('#_form').form('clear');
	$('#_form').form({
				editable : true
			});
	formFocus("_form", "_name");
	var formData = $.extend({}, selected);
	formData = addKeyStartWith(getOriginalData(formData), "_");
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

	loadPhaseSelect(formData._versionId);
	$('#_phaseId').combobox('select', formData._phaseId);

	loadTaskSelect(formData._projectId);
	$('#_beforeTask').combobox('select', formData._beforeTask);

	$('#_form').form('load', {
				startDateShow : dateFormat_1(formData._startDate)
			});
	$('#_form').form('load', {
				endDateShow : dateFormat_1(formData._endDate)
			});
	$('#_phaseId').combobox('select', formData._phaseId);
	/** *****加载显示数据 end********* */
	queryDetail(formData._id);

	$('#dlg').dialog('open');
	$('#dlg').dialog('center');

}

function queryDetail(id, owner) {
	var opts = $("#detail_grid").datagrid("options");

	opts.url = "${contextPath}/task/listTaskDetails?taskId=" + id;

	$("#detail_grid").datagrid("reload");
	var htmlobj = $.ajax({
				url : "${contextPath}/task/listTaskDetail.json?id=" + id,
				async : false
			});
	var str = htmlobj.responseText;
	var obj = $.parseJSON(str);
	$('#showTaskFont').html(obj.taskHour);
	$('#showOverFont').html(obj.overHour);

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

// 打开执行任务窗口
function openUpdateDetail(selected) {
	if (typeof selected == 'number') {
		$("#grid").datagrid("selectRow", selected);
		selected = $("#grid").datagrid("getSelected");
	}
	loadProjectSelect();
	noEdit();
	noEditForTaskdetail();
	$('#modified').datebox({
				editable : false
			});
	$("#task_detail").hide();
	$("#dialog_toolbar").hide();
	$("#dialog_toolbar_detail").show();
	$("#task_detail_list").hide();

	$("#task_detail").show();

	if (isOwenr(selected.id)) {
		canEditForTaskdetail();
	}

	$('#_form').form('clear');

	$('#dlg').dialog('open');
	$('#dlg').dialog('center');

	$('#_form').form({
				editable : true
			});
	formFocus("_form", "_name");
	var formData = $.extend({}, selected);
	formData = addKeyStartWith(getOriginalData(formData), "_");
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

	loadPhaseSelect(formData._versionId);
	$('#_phaseId').combobox('select', formData._phaseId);
	loadTaskSelect(formData._projectId);
	$('#_beforeTask').combobox('select', formData._beforeTask);

	$('#_form').form('load', {
				startDateShow : dateFormat_1(formData._startDate)
			});
	$('#_form').form('load', {
				endDateShow : dateFormat_1(formData._endDate)
			});
	$('#_phaseId').combobox('select', formData._phaseId);

	if (formData._status == 0) {
		$("#task_detail").hide();
		$("#doTask").show();
		$("#pauseTask").hide();
		$("#updateDetail").hide();
		$("#complate").hide();
	}

	if (formData._status == 2) {
		$("#task_detail").show();
		$("#doTask").show();

		$("#complate").show();
		$("#pauseTask").hide();
		$("#updateDetail").hide();
	}

	if (formData._status == 1 || formData._status == 4) {
		$('#_progressShow').progressbar({
					value : parseInt(formData._progress)
				});

		$("#task_detail").show();
		$("#doTask").hide();
		$("#pauseTask").show();
		$("#updateDetail").show();
		$("#complate").show();
		$('#detail_form').form('clear');
		getDetailInfo(formData._id);
	}

	if (formData._status == 3) {
		$('#_progressShow').progressbar({
					value : parseInt(formData._progress)
				});
		$("#complate").hide();
		$("#task_detail").show();
		$("#doTask").hide();
		$("#pauseTask").hide();
		$("#updateDetail").hide();
		$('#detail_form').form('clear');
		getDetailInfo(formData._id);
	}
	if (isProjectManager()) {
		$("#modified").datebox({
					disabled : false
				});
	}
	$('#detail_form').form('load', {
				modified : dateFormat_1(new Date())
			});

}

function isOwenr(id) {
	var htmlobj = $.ajax({
				url : "${contextPath}/task/isOwner.json?id=" + id,
				async : false
			});
	var str = htmlobj.responseText;
	var obj = $.parseJSON(str);
	return obj;
}
function startTask() {
	var id = $("#_id").val();
	$.ajax({
				type : "POST",
				url : "${contextPath}/task/updateTaskStatus?id=" + id,
				processData : true,
				dataType : "json",
				async : true,
				success : function(data) {
					if (data.code == "200") {
						if (window.frames[0]) {
							window.frames[0].reloadGrid();
						} else {
							$('#grid').datagrid('reload');
						}
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

function saveTaskDetail() {
	var planTimeStr = $("#planTimeStr").val();
	var formDate = $("#detail_form").serialize();
	if ($('#overHourStr').textbox('getValue') == "") {
		$('#detail_form').form('load', {
					overHourStr : 0
				});
	}
	var planTimeStr = $("#planTimeStr").val();

	if (!$('#detail_form').form("validate")) {
		return;
	}
	$('#dlg').dialog('close');
	$.ajax({
				type : "POST",
				url : "${contextPath}/task/updateTaskDetails?planTimeStr=" + planTimeStr,
				data : formDate,
				processData : true,
				dataType : "json",
				async : true,
				success : function(data) {
					if (data.code == "200") {
						if (window.frames[0]) {
							window.frames[0].reloadGrid();
						} else {
							$('#grid').datagrid('reload');
						}
						LogUtils.saveLog(LOG_MODULE_OPS.PERFORM_PROJECT_VERSION_PHASE_TASK, "执行任务:" + data.data + ":成功", function() {
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

// 工时填写校验
$.extend($.fn.validatebox.defaults.rules, {
			taskHours : {
				validator : function(value, param) {
					var s = $("input[name=" + param[0] + "]").val();

					if (value > 0 || s > 0) {
						if (value > 8) {
							$.fn.validatebox.defaults.rules.taskHours.message = '当日所填任务工时只能是8小时！';
							return false;

						} else {
							return true;
						}

					} else {
						$.fn.validatebox.defaults.rules.taskHours.message = '工时或加班工时必选填写其中一项！';
						return false;
					}
				},
				message : ''
			}
		})
$.extend($.fn.validatebox.defaults.rules, {
			overHours : {
				validator : function(value, param) {
					var s = $("input[name=" + param[0] + "]").val();
					if (value > 0 || s > 0) {
						return true;
					} else {
						$.fn.validatebox.defaults.rules.overHours.message = '工时或加班工时必选填写其中一项！';
						return false;
					}
				},
				message : ''
			}
		});

// 判断是否是项目经理
function isProjectManager() {
	var htmlobj = $.ajax({
				url : "${contextPath}/task/isProjectManger.json",
				async : false
			});
	var str = htmlobj.responseText;
	var obj = $.parseJSON(str);
	return obj;
}

function dateFormat_1(longTypeDate) {
	var dateType = "";
	var date = new Date();
	date.setTime(longTypeDate);
	dateType += date.getFullYear(); // 年
	dateType += "-" + getMonth(date); // 月
	dateType += "-" + getDay(date); // 日
	return dateType;
}

// 返回 01-12 的月份值
function getMonth(date) {
	var month = "";
	month = date.getMonth() + 1; // getMonth()得到的月份是0-11
	if (month < 10) {
		month = "0" + month;
	}
	return month;
}
// 返回01-30的日期
function getDay(date) {
	var day = "";
	day = date.getDate();
	if (day < 10) {
		day = "0" + day;
	}
	return day;
}

// 创建者
function isCreater(id) {
	var htmlobj = $.ajax({
				url : "${contextPath}/task/isCreater.json?id=" + id,
				async : false
			});
	var str = htmlobj.responseText;
	var obj = $.parseJSON(str);
	return obj;
}

function defaultValue() {
	$('#_projectId').combobox({
				onLoadSuccess : function() {
					var projectId = $('#_projectId').combobox('getValue');
					if (projectId == "") {
						$('#_projectId').combobox('select', "----请选择---");
						$('#_versionId').combobox('select', "----请选择---");
						$('#_phaseId').combobox('select', "----请选择---");
						$('#_owner').combobox('select', "----请选择---");
						$('#_type').combobox('select', "----请选择---");
					}
				}

			})
}

function isDefaultValue(val) {
	if (val == "----请选择---") {
		return true;
	}
}

// 是否填写过工时
function isTask(id) {
	var htmlobj = $.ajax({
				url : "${contextPath}/task/isTask.json?id=" + id,
				async : false
			});
	var str = htmlobj.responseText;
	var obj = $.parseJSON(str);
	return obj;
}

function pauseTaskStatus() {
	var id = $("#_id").val();
	$.ajax({
				type : "POST",
				url : "${contextPath}/task/pauseTaskStatus?id=" + id,
				processData : true,
				dataType : "json",
				async : true,
				success : function(data) {
					if (data.code == "200") {
						if (window.frames[0]) {
							window.frames[0].reloadGrid();
						} else {
							$('#grid').datagrid('reload');
						}
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

function complate(id) {
	$.messager.confirm('提示', '提前完成任务，该任务将不能填写工时，确定要执行该操作？', function(f) {
				if (!f) {
					return false;
				}
				if (isTask(id)) {
					$.ajax({
								type : "POST",
								url : "${contextPath}/task/complateTask?id=" + id,
								processData : true,
								dataType : "json",
								async : true,
								success : function(data) {
									if (data.code == "200") {
										if (window.frames[0]) {
											window.frames[0].reloadGrid();
										} else {
											$('#grid').datagrid('reload');
										}
										$('#dlg').dialog('close');
										LogUtils.saveLog(LOG_MODULE_OPS.COMPLETE_PROJECT_VERSION_PHASE_TASK, "完成任务：" + data.data + ":成功", function() {
												});
									} else {
										$.messager.alert('错误', data.result);
									}
								},
								error : function() {
									$.messager.alert('错误', '远程访问失败');
								}
							});
				} else {
					$.messager.alert('错误', '任务工时加班工时其中一项不能为0');
				}
			});
}

$.extend($.fn.validatebox.defaults.rules, {
			checkDate : {
				validator : function(value, param) {
					var nowDate = new Date();
					var d1 = new Date(value.replace(/\-/g, "\/"));
					return d1 < nowDate;
				},
				message : '不能选择未来日期！'
			}
		});
