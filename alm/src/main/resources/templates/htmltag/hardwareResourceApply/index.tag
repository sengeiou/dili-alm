function deptMemberFormatter(value, row, index) {
	return row.applicationDepartmentId + '/' + row.applicantId;
}

function envFormatter(v, r, i) {
	var content = '';
	$(v).each(function(index, item) {
				content += item.environment + ',';
			});
	content = content ? content.substring(0, content.length - 1) : '';
	return content;
}

//初始化
function loadProject() {
	var data = $('#projectId').combobox('getData');
	if (data) {
		changeProjectSetValue(data[0].value);
	}
}
//项目框有变
function changeProject(){
	var id = $('#projectId').combobox('getValue');
	changeProjectSetValue(id);
}


function changeProjectSetValue(id) {
		$.post('${contextPath!}/project/listViewData.json', {
					id :id
				}, function(res) {
					if (res && res.length > 0) {
						var p = res[0];
						$('#projectManager').textbox('initValue', p.projectManager);
						$('#serialNumber').textbox('initValue', p.serialNumber);
					}
				}, 'json');
}
// 打开新增窗口
function openInsert() {

	$('#win').dialog({
				title : '申请资源',
				width : 830,
				height : 500,
				href : '${contextPath!}/hardwareResourceApply/add',
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
							  //
							}
						}, {
							text : '取消',
							handler : function() {
								$('#win').dialog('close');
							}
						}, {
							text : '提交',
							handler : function() {
								//$.post('${contextPath!}/projectOnlineApply/saveAndSubmit');
							}
						}]
			});
			
}

// 打开修改窗口
function openUpdate() {
    var id = 1;
 /*   if(id==null){
    	var selected = $("#grid").datagrid("getSelected");
		if (null == selected) {
			$.messager.alert('警告', '请选中一条数据');
			return;
		}
    }*/
    
	$('#win').dialog({
				title : '修改资源',
				width : 830,
				height : 500,
				href : '${contextPath!}/hardwareResourceApply/toUpdate?id=' + id,
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
							}
						}, {
							text : '取消',
							handler : function() {
								$('#win').dialog('close');
							}
						}, {
							text : '提交',
							handler : function() {
								$.post('${contextPath!}/projectOnlineApply/saveAndSubmit', {
											id : id
										});
							}
						}]
			});
}

/********* 
// 打开新增窗口
function openInsert() {
	$('#dlg').dialog('open');
	$('#dlg').dialog('center');
	$('#_form').form('clear');
	formFocus("_form", "_projectId");
}

// 打开修改窗口
function openUpdate() {
	var selected = $("#grid").datagrid("getSelected");
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	$('#dlg').dialog('open');
	$('#dlg').dialog('center');
	formFocus("_form", "_projectId");
	var formData = $.extend({}, selected);
	formData = addKeyStartWith(getOriginalData(formData), "_");
	$('#_form').form('load', formData);
}
*********/
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
function del() {
	var selected = $("#grid").datagrid("getSelected");
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
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
		/**	bindFormEvent("form", "projectId", queryGrid);
			bindFormEvent("_form", "_projectId", saveOrUpdate, function() {
						$('#dlg').dialog('close');
					});**/
			if (document.addEventListener) {
				document.addEventListener("keyup", getKey, false);
			} else if (document.attachEvent) {
				document.attachEvent("onkeyup", getKey);
			} else {
				document.onkeyup = getKey;
			}
		});