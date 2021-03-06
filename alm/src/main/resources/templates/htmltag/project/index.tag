// 编辑行索引
var editId = undefined;

var userId = '${user.id!}';
userId = parseInt(userId);

function timeProgressFormatter(val, row, index) {
	var startDate = new Date(row.startDate + ' 00:00:00');
	var endDate = new Date(row.endDate + ' 00:00:00');
	var now = new Date();
	now.setHours(0);
	now.setMinutes(0);
	now.setSeconds(0);
	var rate = (now.getTime() - startDate.getTime()) / (endDate.getTime() - startDate.getTime()) * 100;
	var progress = rate > 100 ? 100 : rate;
	var htmlstr = '<div style="width: 100px; height:20px;border: 1px solid #299a58;"><div style="width:' + progress + 'px; height:20px; background-color: #299a58;"><span>' + Math.round(rate)
			+ '%</span></div></div>';
	return htmlstr;
}

function addClearIcon() {
	$(this).combobox({
				icons : [{
							iconCls : 'icon-remove',
							handler : function(e) {
								$(e.data.target).textbox('initValue', '');
							}
						}]
			});
}

function progressFormatter(value, rowData, rowIndex) {
	var progress = value;
	if (value > 100) {
		progress = 100;
	}
	var htmlstr = '<div style="width: 100px; height:20px;border: 1px solid #299a58;"><div style="width:' + progress + 'px; height:20px; background-color: #0099FF;"><span>' + value
			+ '%</span></div></div>';
	return htmlstr;
}

function projectNameFormatter(value, row) {
	return '<a href="${contextPath!}/project/detail.html?id=' + row.id + '&backUrl=' + encodeURIComponent('${contextPath!}/project/index.html') + '">' + value + '</a>';
}

function taskFormatter(value, row, index) {
	return '<a href="${contextPath!}/task/index.html?projectId=' + row.id + '">' + value + '</a>';
}

function memberFormatter(value, row, index) {
	var backUrl = encodeURIComponent(window.location);
	return '<a href="${contextPath!}/team/index.html?projectId=' + row.id + '&backUrl=' + backUrl + '">' + value + "</a>";
}

function optFormatter(value, row) {
	var content = '';
	if (row.$_projectState == 1) {
		if (row.projectManager == userId) {
			content += '<a style="padding:0px 5px;"  href="javascript:void(0);" onclick="pause(' + row.id + ');">暂停</a>';
		} else {
			content += '<span style="padding:0px 5px;">暂停</span>';
		}
	}
	if (row.$_projectState == 3) {
		if (row.projectManager == userId) {
			content += '<a style="padding:0px 5px;" href="javascript:void(0);" onclick="resume(' + row.id + ');">重启</a>';
		} else {
			content += '<span style="padding:0px 5px;">重启</span>';
		}
	}
	if (row.$_projectState == 4) {
		content += '<span style="padding:0px 5px;">管理</span>';
	} else {
		content += '<a href="${contextPath!}/project/detail.html?id=' + row.id + '&editable=true&backUrl=' + encodeURIComponent('${contextPath!}/project/index.html') + '">管理</a>'
	}
	return content;
}

function pause(id) {
	$.messager.confirm('提示', '确定要暂停该项目吗？', function(f) {
				if (f) {
					$.post('${contextPath!}/project/pause', {
								id : id
							}, function(res) {
								if (res.success) {
									var row = $('#grid').treegrid('find', id);
									row.projectState = '暂停中';
									row.$_projectState = 3;
									$('#grid').treegrid('update', {
												id : id,
												row : row
											});
									$.messager.alert('提示', '操作成功');
								} else {
									$.messager.alert('提示', res.result);
								}
							});
				}
			});
}

function resume(id) {
	$.messager.confirm('提示', '确定要重启该项目吗？', function(f) {
				if (f) {
					$.post('${contextPath!}/project/resume', {
								id : id
							}, function(res) {
								if (res.success) {
									var row = $('#grid').treegrid('find', id);
									row.projectState = '进行中';
									row.$_projectState = 1;
									$('#grid').treegrid('update', {
												id : id,
												row : row
											});
									$.messager.alert('提示', '操作成功');
								} else {
									$.messager.alert('提示', res.result);
								}
							});
				}
			});
}

function isEditing() {
	return undefined != editId;
}

var loadFilter = function(data, parentId) {
	if (parentId != undefined) {
		return data;
	}
	var getChildren = function(parent) {
		var children = new Array();
		$(data).each(function(i, el) {
					if (parent.id == el.parentId) {
						var obj = new Object();
						$.extend(true, obj, el);
						obj.children = getChildren(obj);
						children.push(obj);
					}
				});
		return children;
	};
	var target = new Array();
	$(data).each(function(i, el) {
				if (!el.parentId) {
					var obj = new Object();
					$.extend(true, obj, el);
					obj.children = getChildren(obj);
					target.push(obj);
				}
			});
	return target;
}

// 结束行编辑
function endEditing() {
	if (editId == undefined) {
		return true
	}
	if (projectGrid.treegrid('validateRow', editId)) {
		projectGrid.treegrid('endEdit', editId);
		editId = undefined;
		return true;
	} else {
		return false;
	}
}

// 打开新增窗口
function openInsert(isRoot) {
	if (!dataAuth.addProject) {
		return;
	}
	if (!endEditing()) {
		$.messager.alert('警告', '有数据正在编辑');
		return;
	}
	var node = undefined;
	if (!isRoot) {
		node = projectGrid.treegrid('getSelected');
		if (!node) {
			$.messager.alert('警告', '请选择一条记录');
			return;
		}
	}
	editId = 'temp';
	projectGrid.treegrid('append', {
				parent : node ? node.id : null,
				data : [{
							id : 'temp'
						}]
			});

	projectGrid.treegrid('select', 'temp');
	projectGrid.treegrid('beginEdit', 'temp');
}

// 打开修改窗口
function openUpdate() {
	if (!dataAuth.updateProject) {
		return;
	}
	var selected = projectGrid.treegrid("getSelected");
	if (!selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	if (endEditing()) {
		projectGrid.treegrid('select', selected.id).treegrid('beginEdit', selected.id);
		editId = selected.id;
	}
}

// 根据主键删除
function del() {
	if (!dataAuth.deleteProject) {
		return;
	}
	var selected = projectGrid.treegrid("getSelected");
	if (null == selected) {
		$.messager.alert('警告', '请选中一条数据');
		return;
	}
	$.messager.confirm('确认', '删除该项目会删除该项目关联的团队，您确定想要删除该项目吗？', function(r) {
				if (r) {
					$.ajax({
								type : "POST",
								url : '${contextPath!}/project/delete',
								data : {
									id : selected.id
								},
								processData : true,
								dataType : "json",
								async : true,
								success : function(data) {
									if (data.code == "200") {
										projectGrid.treegrid('remove', selected.id);
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
	var opts = projectGrid.treegrid("options");
	if (null == opts.url || "" == opts.url) {
		opts.url = "${contextPath!}/project/listPage";
	}
	if (!$('#form').form("validate")) {
		return;
	}
	var param = bindMetadata("grid", true);
	var formData = $("#form").serializeObject();
	$.extend(param, formData);
	projectGrid.treegrid("load", param);
}

// 清空表单
function clearForm() {
	$('#form').form('clear');
	$('#originator').textbox('initValue', '');
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
	switch (keycode) {
		case 46 :
			if (isEditing())
				return;
			var selected = projectGrid.treegrid("getSelected");
			if (selected && selected != null) {
				del();
			}
			break;
		case 13 :
			if ($("#smDialog").parent().is(":hidden")) {
				endEditing();
			}
			break;
		case 27 :
			cancelEdit();
			break;
	}
}

function resizeColumn(original) {
	if (original) {
		projectGrid.treegrid('resizeColumn', [{
							field : 'name',
							width : '15%'
						}, {
							field : 'projectManager',
							width : '10%'
						}, {
							field : 'testManager',
							width : '10%'
						}, {
							field : 'productManager',
							width : '10%'
						}]);
	} else {
		projectGrid.treegrid('resizeColumn', [{
							field : 'name',
							width : '20%'
						}, {
							field : 'projectManager',
							width : '15%'
						}, {
							field : 'testManager',
							width : '15%'
						}, {
							field : 'productManager',
							width : '15%'
						}]);
	}
}

function onBeginEdit(row) {
	if (row.id == 'temp') {

		return true;
	}
	var editor = projectGrid.treegrid('getEditor', {
				id : row.id,
				field : 'type'
			});
	editor.target.textbox('setValue', row.$_type);
	editor.target.textbox('setText', row.type);
	editor = projectGrid.treegrid('getEditor', {
				id : row.id,
				field : 'projectManager'
			});
	editor.target.textbox('setValue', row.$_projectManager);
	editor.target.textbox('setText', row.projectManager);
	editor = projectGrid.treegrid('getEditor', {
				id : row.id,
				field : 'testManager'
			});
	editor.target.textbox('setValue', row.$_testManager);
	editor.target.textbox('setText', row.testManager);
	editor = projectGrid.treegrid('getEditor', {
				id : row.id,
				field : 'productManager'
			});
	editor.target.textbox('setValue', row.$_productManager);
	editor.target.textbox('setText', row.productManager);
}

function selectFormMember(id) {
	window.smDialog = $('#smDialog');
	smDialog.dialog({
				title : '用户选择',
				width : 800,
				height : 400,
				href : '${contextPath!}/member/members',
				modal : true,
				buttons : [{
							text : '确定',
							handler : function() {
								var selected = memberList.treegrid('getSelected');
								$('#' + id).textbox('setValue', selected.id);
								$('#' + id).textbox('setText', selected.realName);
								smDialog.dialog('close');
							}
						}, {
							text : '取消',
							handler : function() {
								smDialog.dialog('close');
							}
						}],
				onLoad : function() {
					window.memberList = $('#smGridList');
				}
			});
}

var columnFormatter = function(value, row) {
	var content = '<input type="button" id="btnSave' + row.id + '" style="margin:0px 4px;display:none;" value="保存" onclick="javascript:endEditing();">';
	content += '<input type="button" id="btnCancel' + row.id + '" style="margin:0px 4px;display:none;" value="取消" onclick="javascript:cancelEdit();">';
	return content;
};

/**
 * 显示编辑行最后一列的操作按钮
 * 
 * @param {}
 *            index 行索引
 */
function showOptButtons(id) {
	$('#btnSave' + id + ',#btnCancel' + id).show();
}

/**
 * 显示编辑行最后一列的操作按钮
 * 
 * @param {}
 *            index 行索引
 */
function hideOptButtons(id) {
	$('#btnSave' + id + ',#btnCancel' + id).hide();
}

/**
 * 开始编辑行的毁掉函数
 * 
 * @param {}
 *            index 行索引
 * @param {}
 *            row 行数据
 */
function onBeforeEdit(row) {
	if (row.id != 'temp') {
		window.oldRecord = new Object();
		$.extend(true, oldRecord, row);
	}
	resizeColumn();
	hideCMAndShowOpt(row.id);
}

function hideCMAndShowOpt(rowId) {
	projectGrid.treegrid('hideColumn', 'created');
	projectGrid.treegrid('hideColumn', 'modified');
	showOptButtons(rowId);
	projectGrid.treegrid('showColumn', 'opt');
}

function showCMAndHideOpt(rowId) {
	projectGrid.treegrid('showColumn', 'created');
	projectGrid.treegrid('showColumn', 'modified');
	hideOptButtons(rowId);
	projectGrid.treegrid('hideColumn', 'opt');
}

/**
 * 取消行编辑
 */
function cancelEdit() {
	oldRecord = undefined;
	if (editId == undefined) {
		return;
	}
	projectGrid.treegrid('cancelEdit', editId);
};

/**
 * 取消行编辑回调方法
 * 
 * @param {}
 *            index
 * @param {}
 *            row
 */
function onCancelEdit(row) {
	resizeColumn(true);
	showCMAndHideOpt(row.id);
	editId = undefined;
	if (row.id == 'temp') {
		projectGrid.treegrid('remove', row.id);
		return;
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
function onAfterEdit(row, changes) {
	var isValid = projectGrid.treegrid('validateRow', row.id);
	if (!isValid) {
		return false;
	}
	hideOptButtons(row.id);
	showOptButtons(row.id);
	insertOrUpdateProject(row, changes);
	oldRecord = undefined;
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
function insertOrUpdateProject(row, changes) {
	var postData;
	var oldRecord = window.oldRecord;
	var postData = new Object();
	$.extend(true, postData, row);
	var url = '${contextPath!}/project/';
	postData = getOriginalData(row);
	if (postData.id == 'temp') {
		postData.id = null;
		postData.parentId = row["_parentId"];
		url += 'insert';
	} else {
		url += 'update'
	}

	$.post(url, postData, function(data) {

				if (data.code != 200) {
					if (oldRecord) {
						projectGrid.treegrid('update', {
									id : row.id,
									row : oldRecord
								});
					} else {
						projectGrid.treegrid('remove', row.id);
					}
					$.messager.alert('提示', data.result);
					return;
				}

				var date = new Date().Format("yyyy-MM-dd HH:mm:ss");
				if (postData.id == undefined) {
					projectGrid.treegrid('remove', 'temp');
					row.id = data.data.id;
					row.created = date;
					row.modified = date;
					row.parentId = "";
					row._parentId = "";

					projectGrid.treegrid('append', {
								parent : data.data.parentId,
								data : [row]
							});
				} else {
					row.modified = date;
					projectGrid.treegrid('update', {
								id : postData.id,
								row : row
							});
				}
			}, 'json');
}

/**
 * 结束编辑回调方法
 * 
 * @param {}
 *            index
 * @param {}
 *            row
 */
function onEndEdit(row) {
	var editor = $(this).treegrid('getEditor', {
				id : row.id,
				field : 'type'
			});
	row.type = editor.target.combobox('getText');
	row.$_type = editor.target.combobox('getValue');
	editor = $(this).treegrid('getEditor', {
				id : row.id,
				field : 'projectManager'
			});
	row.projectManager = editor.target.textbox('getText');
	row.$_projectManager = editor.target.textbox('getValue');
	editor = $(this).treegrid('getEditor', {
				id : row.id,
				field : 'testManager'
			});
	row.testManager = editor.target.textbox('getText');
	row.$_testManager = editor.target.textbox('getValue');
	editor = $(this).treegrid('getEditor', {
				id : row.id,
				field : 'productManager'
			});
	row.productManager = editor.target.textbox('getText');
	row.$_productManager = editor.target.textbox('getValue');
	resizeColumn(true);
	showCMAndHideOpt(row.id);
}

function editorCallback(field) {
	var selected = projectGrid.treegrid("getSelected");
	var editor = projectGrid.treegrid('getEditor', {
				id : selected.id,
				field : field
			});
	$(editor.target).attr("id", field + "_" + selected.id);
	showMembersDlg(field + "_" + selected.id);
}

/**
 * 绑定页面回车事件，以及初始化页面时的光标定位
 * 
 * @formId 表单ID
 * @elementName 光标定位在指点表单元素的name属性的值
 * @submitFun 表单提交需执行的任务
 */
$(function() {
			$('#originator').textbox('addClearBtn', 'icon-clear');
			bindFormEvent("form", "name", queryGrid);
			if (document.addEventListener) {
				document.addEventListener("keyup", getKey, false);
			} else if (document.attachEvent) {
				document.attachEvent("onkeyup", getKey);
			} else {
				document.onkeyup = getKey;
			}
			window.projectGrid = $('#grid');
			queryGrid();
		});