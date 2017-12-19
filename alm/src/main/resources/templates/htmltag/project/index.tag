// 编辑行索引
var editId = undefined;

function projectNameFormatter(value, row) {
	if (!row.manager) {
		return value;
	}
	return '<a href="${contextPath!}/project/detail.html?id=' + row.id + '">' + value + '</a>';
}

function optFormatter(value, row) {
	if (!row.manager) {
		return '';
	}
	return '<a href="${contextPath!}/project/detail.html?id=' + row.id + '">管理</a>';
}

function progressFormatter(value, rowData, rowIndex) {
	var htmlstr = '<div class="progressbar-text" style="width:100%;">' + value + '%</div><div class="progressbar-value" style="width:\'' + value + '%\';"> </div>';
	return htmlstr;
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
				href : '${contextPath!}/member/members.html',
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