var paramCount=0;

function optFormatter(value, row, index) {
	var content = '';
	if (row.editable) {
		content += '<a style="padding:0px 2px;" href="javascript:void(0);" onclick="openUpdate(' + row.id + ');">编辑</a>';
		content += '<a style="padding:0px 2px;" href="javascript:void(0);" onclick="del(' + row.id + ');">删除</a>';
	}
	if (row.testConfirmable) {
		content += '<a href="javascript:void(0);" onclick="textConfirm(' + row.id + ');">测试确认</a>';
	}
	if (row.startExecutable) {
		content += '<a href="javascript:void(0);" onclick="textConfirm(' + row.id + ');">开始执行</a>';
	}
	if (row.confirmExecutable) {
		content += '<a href="javascript:void(0);" onclick="confirmExecute(' + row.id + ');">确认执行</a>';
	}
	if (row.verifiable) {
		content += '<a href="javascript:void(0);" onclick="verify(' + row.id + ');">验证</a>';
	}
	return content;

}

function appendSubsystem(){
	paramCount++;
	var content='<div class="subsystem"><tr>'+
						'<td class="table-title">系统名称</td>'+
						'<td class="table-combo" style="padding: 0"><input class="easyui-combobox" name="projectName"'+
							'style="width: 100%; text-align: center;" url="${contextPath!}/project/list.json" textField="name" valueField="name" /></td>'+
						'<td class="table-title">负责人</td>'+
						'<td class="table-combo" colspan="3" style="padding: 0"><select class="easyui-combobox" name="state"'+
							'url="${contextPath!}/member/members" textField="realName" valueField="id" style="width: 100%; text-align: center;">'+
						'</select></td>'
					'</tr>'+
					'<tr>'+
						'<td class="table-title" colspan="2">git地址</td>'+
						'<td colspan="4"><input class="easyui-textbox" name="git[0]"></td>'+
					'</tr>'+
					'<tr>'+
						'<td class="table-title" colspan="2">分支</td>'+
						'<td colspan="4"><input class="easyui-textbox" name="branch[0]"></td>'+
					'</tr>'+
					'<tr>'+
						'<td class="table-title" colspan="2">SQL脚本</td>'+
						'<td class="table-combo" colspan="4" style="padding: 0"><input class="easyui-filebox" name="sqlFileId[0]"'+
							'data-options="prompt:\'添加附件...\',buttonText:\'选择\'" style="width: 100%"></td>'+
					'</tr>'+
					'<tr>'+
						'<td class="table-title" colspan="2">系统启动脚本</td>'+
						'<td class="table-combo" colspan="4" style="padding: 0"><input class="easyui-filebox" name="startupScriptFileId[0]"'+
							'data-options="prompt:\'添加附件...\',buttonText:\'选择\'" style="width: 100%"></td>'+
					'</tr>'+
					'<tr>'+
						'<td class="table-title" colspan="2">依赖系统</td>'
						'<td class="table-combo" colspan="4" style="padding: 0"><input class="easyui-textbox" name="dependencySystem[0]" data-options=""'+
							'style="width: 50%"><input class="easyui-filebox" name="dependencySystemFileId[0]"'+
							'data-options="prompt:\'添加附件...\',	buttonText:\'选择\'" style="width: 50%"></td>'+
					'</tr>'+
					'<tr>'+
						<td class="table-title" colspan="2">其他说明</td>
						<td colspan="4"><input class="easyui-textbox" name="otherDescription[0]" data-options="prompt:'添加附件...',buttonText:'选择'"
							style="width: 100%"></td>
					</tr>
					</div>';
}

function loadProject() {
	var data = $('#projectId').combobox('getData');
	if (data) {
		$.post('${contextPath!}/project/listViewData.json', {
					id : data[0].id
				}, function(res) {
					if (res && res.length > 0) {
						var p = res[0];
						$('#projectManager').textbox('initValue', p.projectManager);
						$('#serialNumber').textbox('initValue', p.serialNumber);
						$('#productManager').textbox('initValue', p.productManager);
						$('#businessOwner').textbox('initValue', p.businessOwner);
						$('#testManager').textbox('initValue', p.testManager);
						$('#developManager').textbox('initValue', p.developManager);
					}
				}, 'json');
	}
}

function loadVersion(nval, oval) {
	$('#versionId').combobox('reload', '${contextPath!}/project/version/list?projectId=' + nval);
	$('#versionId').combobox('enable');
	$.post('${contextPath!}/project/listViewData.json', {
				id : nval
			}, function(res) {
				if (res && res.length > 0) {
					var p = res[0];
					$('#projectManager').textbox('initValue', p.projectManager);
					$('#serialNumber').textbox('initValue', p.serialNumber);
					$('#productManager').textbox('initValue', p.productManager);
					$('#businessOwner').textbox('initValue', p.businessOwner);
					$('#testManager').textbox('initValue', p.testManager);
					$('#developManager').textbox('initValue', p.developManager);
				}
			}, 'json');
}

// 打开新增窗口
function openInsert() {
	$('#win').dialog({
				title : '上线申请',
				width : 800,
				height : 600,
				href : '${contextPath!}/projectOnlineApply/add',
				modal : true,
				buttons : [{
							text : '保存',
							handler : function() {
								if (!$('#versionForm').form('validate')) {
									return;
								}
								var data = $("#versionForm").serializeArray();
								$.ajax({
											type : "POST",
											url : '${contextPath!}/project/version/insert',
											data : data,
											success : function(data) {
												if (data.code == 200) {
													try {
														LogUtils.saveLog(LOG_MODULE_OPS.ADD_PROJECT_VERSION, "新增项目版本:" + data.data.id + ":" + data.data.version + ":成功", function() {
																});
													} catch (e) {
														$.messager.alert('错误', e);
													}
													$('#versionGrid').datagrid('appendRow', data.data);
													$('#versionGrid').datagrid('acceptChanges');
													countVersionGrid();
													if ($('#versionForm input[name=fileIds]').length > 0) {
														$('#fileGrid').datagrid('reload');
														countFileGrid();
													}
													$('#win').dialog('close');
												} else {
													$.messager.alert('错误', data.result);
												}
											},
											error : function() {
												$.messager.alert('错误', '远程访问失败');
											}
										});
							}
						}, {
							text : '取消',
							handler : function() {
								$('#win').dialog('close');
							}
						}, {
							text : '提交',
							handler : function() {

							}
						}]
			});
}

// 打开修改窗口
function openUpdate(id) {
	var selected = $("#grid").datagrid("getSelected");
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	$('#dlg').dialog('open');
	$('#dlg').dialog('center');
	formFocus("_form", "_projectName");
	var formData = $.extend({}, selected);
	formData = addKeyStartWith(getOriginalData(formData), "_");
	$('#_form').form('load', formData);
}

function saveOrUpdate() {
	if (!$('#_form').form("validate")) {
		return;
	}
	var _formData = removeKeyStartWith($("#_form").serializeObject(), "_");
	var _url = null;
	// 没有id就新增
	if (_formData.id == null || _formData.id == "") {
		_url = "${contextPath}/projectOnlineApply/insert";
	} else {// 有id就修改
		_url = "${contextPath}/projectOnlineApply/update";
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
function del(id) {
	var selected = $("#grid").datagrid("getSelected");
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	$.messager.confirm('确认', '您确认想要删除记录吗？', function(r) {
				if (r) {
					$.ajax({
								type : "POST",
								url : "${contextPath}/projectOnlineApply/delete",
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
		opts.url = "${contextPath}/projectOnlineApply/listPage";
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
			// $('#applicantId').textbox('addClearBtn', 'icon-clear');
			bindFormEvent("form", "projectName", queryGrid);
			bindFormEvent("_form", "_projectName", saveOrUpdate, function() {
						$('#dlg').dialog('close');
					});
			if (document.addEventListener) {
				document.addEventListener("keyup", getKey, false);
			} else if (document.attachEvent) {
				document.attachEvent("onkeyup", getKey);
			} else {
				document.onkeyup = getKey;
			}
			queryGrid();
		});