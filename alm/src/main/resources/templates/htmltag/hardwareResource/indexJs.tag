var userId = '${user.id!}';
userId = parseInt(userId);

var updateDate = dateFormat_1(new Date());

function hardwareResourceShow() {

	$("#formList").css("display", "block");
	var opts = $("#dg").datagrid("options");
	opts.url = "${contextPath}/hardwareResource/list";
	var param = bindMetadata("dg", true);

	var formData = removeKeyStartWith($("#_form").serializeObject(), "_");
	$.extend(param, formData);
	$("#dg").datagrid("load", param);
}
function hardwareResourceInsert() {
	var projectIdVal = $("#_projectId").combobox('getValue');
	if (projectIdVal == null || projectIdVal == '') {
		$.messager.alert('错误', '请选择一个项目');
		return;

	}
	$('#_addform').form('clear');
	$("#formCennet").css("display", "block");
	$("#_addprojectId").val($('#_projectId').combobox('getValue'));
	$('#_addisSubmit').val(0);
	$('#_addmaintenanceDate').textbox('setValue', updateDate);

}
function cancel() {
	$('#_addform').form('clear');
	$("#formCennet").css("display", "none");
}
function projectNameFormatter(value, row) {
	return '<a href="${contextPath!}/project/detail.html?id=' + row.id + '&backUrl=' + encodeURIComponent('${contextPath!}/project/index.html') + '">' + value + '</a>';
}
// 打开新增窗口
function openInsert() {
	if (!dataAuth.addHardwareResource && userId == 1) {
		return false;
	}
	$("#formCennet").css("display", "none");
	$("#formList").css("display", "none");
	loadProject();

	$('#dlg').dialog('open');
	$('#dlg').dialog('center');
	$('#_form').form('clear');

}

// 打开列表添加窗口
function openAddUpdate(row) {
	var selected = $("#dg").datagrid('getData').rows[row];
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	$("#formCennet").css("display", "block");
	var formData = $.extend({}, selected);
	formData = addKeyStartWith(getOriginalData(formData), "_add");
	$('#_addform').form('load', formData);
	var updateDate = dateFormat_1(new Date());
	$('#_addmaintenanceDate').textbox('setValue', updateDate);

}

// 打开修改窗口
function openUpdate(row) {
	if (!dataAuth.updateHardwareResource || userId == 1) {
		return false;
	}
	var selected = $("#grid").datagrid('getData').rows[row];
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	loadUpdateProject();
	$('#updatedlg').dialog('open');
	$('#updatedlg').dialog('center');
	var formData = $.extend({}, selected);
	formData = addKeyStartWith(getOriginalData(formData), "_update");
	$('#_updateform').form('load', formData);
	var updateDate = dateFormat_1(new Date());
	$('#_updatemaintenanceDate').textbox('setValue', updateDate);
}
function updateHard(n) {
	if (n == 1) {
		$('#_updateisSubmit').val(1);
	}
	if (!$('#_updateform').form("validate")) {
		return;
	}
	var _formData = removeKeyStartWith($("#_updateform").serializeObject(), "_update");

	$.ajax({
				type : "POST",
				url : "${contextPath}/hardwareResource/update",
				data : _formData,
				processData : true,
				dataType : "json",
				async : true,
				success : function(data) {
					if (data.code == "200") {
						$('#updatedlg').dialog('close');
						$("#grid").datagrid("reload");

					} else {
						$.messager.alert('错误', data.result);
					}
				},
				error : function() {

					$.messager.alert('错误', '远程访问失败');
				}
			});
}

function submit() {
	var projectIdVal = $("#_projectId").combobox('getValue');
	if (projectIdVal == null || projectIdVal == '') {
		$.messager.alert('错误', '请选择一个项目');
		return;

	}
	$.messager.confirm('确认', '您确认想要提交该项目所有以保存记录吗？', function(r) {
				$.ajax({
							type : "POST",
							url : "${contextPath}/hardwareResource/submit",
							data : {
								projectId : projectIdVal
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
			});
}

function save() {
	// 没有id就新增
	if (!$('#_addform').form("validate")) {
		return;
	}
	var _url;
	var _formData = removeKeyStartWith($("#_addform").serializeObject(), "_add");
	if (_formData.id == null || _formData.id == "") {
		_url = "${contextPath}/hardwareResource/insert";
	} else {// 有id就修改
		_url = "${contextPath}/hardwareResource/update";
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
						cancel();
						hardwareResourceShow();
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
	if (!dataAuth.deleteHardwareResource || userId == 1) {
		return false;
	}
	var selected = null;
	if (index != undefined) {
		selected = $("#grid").datagrid('getData').rows[index];
	} else {
		selected = $('#grid').datagrid('getSelected');
	}
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	if (selected.isSubmit != 0) {
		return false;
	}
	$.messager.confirm('确认', '您确认想要删除记录吗？', function(r) {
				if (r) {
					$.ajax({
								type : "POST",
								url : "${contextPath}/hardwareResource/delete",
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

// 根据项目删除
function AddListDel(row) {
	var selected = $("#dg").datagrid('getData').rows[row];
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	$.messager.confirm('确认', '您确认想要删除记录吗？', function(r) {
				if (r) {
					$.ajax({
								type : "POST",
								url : "${contextPath}/hardwareResource/delete",
								data : {
									id : selected.id
								},
								processData : true,
								dataType : "json",
								async : true,
								success : function(data) {
									if (data.code == "200") {
										cancel();
										hardwareResourceShow();
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
		opts.url = "${contextPath}/hardwareResource/listPage";
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
			bindFormEvent("form", "projectId", queryGrid);
			if (document.addEventListener) {
				document.addEventListener("keyup", getKey, false);
			} else if (document.attachEvent) {
				document.attachEvent("onkeyup", getKey);
			} else {
				document.onkeyup = getKey;
			}
			loadMemberCondition();
			queryGrid();

		})
function loadProject() {

	$('#_projectId').combobox({
				url : "${contextPath!}/hardwareResource/listProjectList.json",
				valueField : 'id',
				textField : 'name',
				editable : false,
				onChange : function(o, n) {
					var rend = projectNum(o);
					var projectNumStr = rend.projectNum;
					var projectTotalStr = rend.projectTotal;
					$("#_projectCost").textbox('setValue', projectTotalStr);
					$("#_projectNum").textbox('setValue', projectNumStr);
					hardwareResourceShow();
				}
			});
}

function loadUpdateProject() {

	$('#_updateprojectId').combobox({
				url : "${contextPath!}/hardwareResource/listProjectList.json",
				valueField : 'id',
				textField : 'name',
				editable : false,
				onChange : function(o, n) {
					// 获取到的项目ID_projectIdText_projectId
					var rend = projectNum(o);
					var projectNumStr = rend.projectNum;
					var projectTotalStr = rend.projectTotal;
					$("#_updateprojectNum").textbox('setValue', projectNumStr);
					$("#_updateprojectCost").textbox('setValue', projectTotalStr);
					$("#_updateprojectId").val(projectId);

				}
			});
}
function loadShowProject() {

	$('#_showprojectId').combobox({
				url : "${contextPath!}/hardwareResource/listProjectList.json",
				valueField : 'id',
				textField : 'name',
				editable : false,
				onChange : function(o, n) {
					// 获取到的项目ID_projectIdText_projectId
					var rend = projectNum(o);

					var projectNumStr = rend.projectNum;
					var projectTotalStr = rend.projectTotal;
					$("#_showprojectNum").textbox('setValue', projectNumStr);
					$("#_showprojectCost").textbox('setValue', projectTotalStr);
					$("#_showprojectId").val(projectId);

				}
			});
}
function loadMemberCondition() {
	$('#maintenanceOwner').combobox({
				url : "${contextPath!}/hardwareResource/listTreeMember.json",
				valueField : 'id',
				textField : 'realName',
				editable : false
			});
}
function projectNum(id) {
	var htmlobj = $.ajax({
				url : "${contextPath!}/hardwareResource/prjectNum.json?id=" + id,
				async : false
			});
	var str = htmlobj.responseText;
	var obj = $.parseJSON(str);
	return obj;
}
function formatOptions(val, row, index) {
	var out = '';
	if (dataAuth.updateHardwareResource) {
		out = "<a href='javascript:void(0)' onclick='openUpdate(" + index + ")'>编辑  </a>";
	} else {
		out = "<span style='padding:5px;color:#8B8B7A;text-decoration:underline;'>编辑</span>";
	}
	if (dataAuth.deleteHardwareResource && row.isSubmit == 0) {
		out += "<a href='javascript:void(0)' onclick='del(" + index + ")'> 删除</a>"
	} else {
		out += "<span style='padding:5px;color:#8B8B7A;text-decoration:underline;'>删除</span>"
	}

	return out;
}

function addOptions(val, row, index) {

	var selected = $("#dg").datagrid('getData').rows[index];
	var out;
	if (selected.isSubmit == 0) {
		out = "<a href='javascript:void(0)' onclick='openAddUpdate(" + index + ")'>编辑  </a><a href='javascript:void(0)' onclick='AddListDel(" + index + ")'> 删除</a>";
	}
	return out;
}

function dateFormat_1(date) {
	var day = date.getDate() > 9 ? date.getDate() : "0" + date.getDate();
	var month = (date.getMonth() + 1) > 9 ? (date.getMonth() + 1) : "0" + (date.getMonth() + 1);
	var hor = date.getHours() > 9 ? date.getHours() : "0" + date.getHours();
	var min = date.getMinutes() > 9 ? date.getMinutes() : "0" + date.getMinutes();
	var sec = date.getSeconds() > 9 ? date.getSeconds() : "0" + date.getSeconds();
	return date.getFullYear() + '-' + month + '-' + day + " " + hor + ":" + min + ":" + sec;
};
$.extend($.fn.validatebox.defaults.rules, {
			amountValidate : {
				validator : function(value) {
					var temp = /^\d+\.?\d{0,2}$/;
					return temp.test(value);
				},
				message : '必须为金额格式最小至小数点后2位!'
			}
		});
$.extend($.fn.validatebox.defaults.rules, {
			checkIPValidate : {
				validator : function(value) {
					var exp = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/;
					var reg = value.match(exp);
					if (reg == null) {
						return false;
					} else {
						return true;
					}
				},
				message : 'IP地址不合法！'
			}
		});
function hardwareResourceNameFormatter(value, row, index) {
	return "<a href='javascript:void(0)' onclick='openShow(" + index + ")'>" + value + "</a>";
}
function openShow(row) {

	var selected = $("#grid").datagrid('getData').rows[row];
	loadShowProject()
	$('#showdlg').dialog('open');
	$('#showdlg').dialog('center');
	var formData = $.extend({}, selected);
	formData = addKeyStartWith(getOriginalData(formData), "_show");
	$('#_showform').form('load', formData);
	var showDate = dateFormat_1(new Date(formData._showmaintenanceDate));
	$('#_showmaintenanceDate').textbox('setValue', showDate);
}