var userId = '${user.id!}';
userId = parseInt(userId);

function serialNumberFormatter(index, row, value) {
	return '<a href="javascript:void(0);" onclick="detail(' + row.id + ');">' + value + '</a>';
}

function detail(id) {
	$('#dlg').dialog({
				href : '${contextPath!}/travelCostApply/detail?id=' + id,
				height : 600,
				width : 800
			});
	$('#dlg').dialog('open');
	$('#dlg').dialog('center');
}

function addCostItem() {
	$('#travelCostGrid').datagrid('appendRow', {});
	var index = $('#travelCostGrid').datagrid('getRows').length - 1;
	$('#travelCostGrid').datagrid('selectRow', index);
	$('#travelCostGrid').datagrid('beginEdit', index);
}

function optFormatter(index, row, value) {
	var content = '';
	if (row.$_applyState == 1) {
		if (row.$_applicantId == userId && userId != 1) {
			content += '<a style="padding:0px 2px;" href="javascript:void(0);" onclick="openUpdate(' + row.id + ');">编辑</a>';
			content += '<a style="padding:0px 2px;" href="javascript:void(0);" onclick="del(' + row.id + ');">删除</a>';
		} else {
			content += '<span style="padding:0px 2px;">编辑</span>';
			content += '<span style="padding:0px 2px;">删除</span>';
		}
	}
	if (row.$_applyState == 2) {
		if (dataAuth.reviewTravelCostApply && userId != 1) {
			content += '<a style="padding:0px 2px;" href="javascript:void(0);" onclick="review(' + row.id + ');">审核</a>';
		} else {
			content += '<span style="padding:0px 2px;">编辑</span>';
		}
	}
	return content;
}

function review(id) {
	$('#dlg').dialog({
				iconCls : 'icon-save',
				href : '${contextPath!}/travelCostApply/review?id=' + id,
				height : 600,
				width : 800,
				buttons : [{
							text : '通过',
							iconCls : 'icon-ok',
							handler : function() {
								$.post('${contextPath!}/travelCostApply/review', {
											id : id,
											result : 1
										}, function(res) {
											if (res.code == 200) {
												$.messager.alert('提示', '操作成功');
												$("#grid").datagrid("reload");
												$('#dlg').dialog('close');
											}
										}, 'json');
							}
						}, {
							text : '拒绝',
							handler : function() {
								$.post('${contextPath!}/travelCostApply/review', {
											id : id,
											result : 0
										}, function(res) {
											if (res.code == 200) {
												$.messager.alert('提示', '操作成功');
												$("#grid").datagrid("reload");
												$('#dlg').dialog('close');
											}
										}, 'json');
							}
						}]

			});
	$('#dlg').dialog('open');
	$('#dlg').dialog('center');
}

function removeCostItem() {
	$('#travelCostGrid').datagrid('deleteRow', $('#travelCostGrid').datagrid('getRows').length - 1);
}

function selectArea(field) {
	$('#areaDlg').dialog({
				iconCls : 'icon-save',
				href : '${contextPath!}/area/select',
				height : 150,
				width : 500,
				title : '出发地选择',
				modal : true,
				buttons : [{
							text : '确定',
							iconCls : 'icon-ok',
							handler : function() {
								var node = $('#travelCostGrid').datagrid('getSelected');
								var index = $('#travelCostGrid').datagrid('getRowIndex', node);
								var editor = $('#travelCostGrid').datagrid('getEditor', {
											index : index,
											field : field
										}).target;
								var value = $('#city').combobox('getValue');
								var text = $('#city').combobox('getText');
								editor.textbox('setValue', value);
								editor.textbox('setText', text);
								$('#areaDlg').dialog('close');
							}
						}]
			});
}

// 打开新增窗口
function openInsert() {
	$('#dlg').dialog({
				iconCls : 'icon-save',
				href : '${contextPath!}/travelCostApply/add',
				height : 600,
				width : 800,
				buttons : [{
							text : '保存',
							iconCls : 'icon-ok',
							handler : function() {

								var _formData = $("#_form").serializeObject();
								var _url = "${contextPath}/travelCostApply/insert";
								var data = $('#travelCostGrid').datagrid('getData');
								var travelCosts = [];
								$(data.rows).each(function(index, item) {
											endPositionEditing($('#travelCostGrid'));
											var travelCost = {};
											var travelCostDetails = [];
											for (prop in item) {
												if (prop == 'travelDayAmount') {
													travelCost.travelDayAmount = item[prop];
												} else if (prop == 'totalAmount') {
													travelCost.totalAmount = item[prop] * 100;
												} else if (prop == 'setOutPlace') {
													travelCost.setOutPlace = item[prop];
												} else if (prop == 'destinationPlace') {
													travelCost.destinationPlace = item[prop];
												} else if (prop == 'setOutPlaceText') {
													continue;
												} else if (prop == 'destinationPlaceText') {
													continue;
												} else {
													travelCostDetails.push({
																costName : prop,
																costAmount : item[prop] * 100
															});
												}
											}
											travelCost.travelCostDetail = travelCostDetails;
											travelCosts.push(travelCost);
										});
								_formData.totalAmount *= 100;
								_formData.travelCost = travelCosts;
								$.ajax({
											type : "POST",
											url : _url,
											contentType : "application/json; charset=utf-8",
											data : JSON.stringify(_formData),
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
						}, {
							text : '取消',
							handler : function() {
								$('#dlg').dialog('close');
							}
						}, {
							text : '提交',
							handler : function() {

								var _formData = $("#_form").serializeObject();
								var _url = "${contextPath}/travelCostApply/saveAndSubmit";
								var data = $('#travelCostGrid').datagrid('getData');
								var travelCosts = [];
								$(data.rows).each(function(index, item) {
											endPositionEditing($('#travelCostGrid'));
											var travelCost = {};
											var travelCostDetails = [];
											for (prop in item) {
												if (prop == 'travelDayAmount') {
													travelCost.travelDayAmount = item[prop];
												} else if (prop == 'totalAmount') {
													travelCost.totalAmount = item[prop] * 100;
												} else if (prop == 'setOutPlace') {
													travelCost.setOutPlace = item[prop];
												} else if (prop == 'destinationPlace') {
													travelCost.destinationPlace = item[prop];
												} else if (prop == 'setOutPlaceText') {
													continue;
												} else if (prop == 'destinationPlaceText') {
													continue;
												} else {
													travelCostDetails.push({
																costName : prop,
																costAmount : item[prop] * 100
															});
												}
											}
											travelCost.travelCostDetail = travelCostDetails;
											travelCosts.push(travelCost);
										});
								_formData.totalAmount *= 100;
								_formData.travelCost = travelCosts;
								$.ajax({
											type : "POST",
											url : _url,
											contentType : "application/json; charset=utf-8",
											data : JSON.stringify(_formData),
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
						}]

			});
	$('#dlg').dialog('open');
	$('#dlg').dialog('center');
}

// 打开修改窗口
function openUpdate(id) {
	if (!id) {
		var selected = $('#travelCostGrid').datagrid('getSelected');
		if (!selected) {
			$.messager.alert('提示', '请选择一条记录');
			return;
		}
		id = selected.id;
	}
	$('#dlg').dialog({
				iconCls : 'icon-save',
				href : '${contextPath!}/travelCostApply/update?id=' + id,
				height : 600,
				width : 800,
				buttons : [{
							text : '保存',
							iconCls : 'icon-ok',
							handler : function() {
								var _formData = $("#_form").serializeObject();
								var _url = "${contextPath}/travelCostApply/update?id=" + id;
								var data = $('#travelCostGrid').datagrid('getData');
								var travelCosts = [];
								$(data.rows).each(function(index, item) {
											endPositionEditing($('#travelCostGrid'));
											var travelCost = {};
											var travelCostDetails = [];
											for (prop in item) {
												if (prop == 'travelDayAmount') {
													travelCost.travelDayAmount = item[prop];
												} else if (prop == 'totalAmount') {
													travelCost.totalAmount = item[prop] * 100;
												} else if (prop == 'setOutPlace') {
													travelCost.setOutPlace = item[prop];
												} else if (prop == 'destinationPlace') {
													travelCost.destinationPlace = item[prop];
												} else if (prop == 'setOutPlaceText') {
													continue;
												} else if (prop == 'destinationPlaceText') {
													continue;
												} else {
													travelCostDetails.push({
																costName : prop,
																costAmount : item[prop] * 100
															});
												}
											}
											travelCost.travelCostDetail = travelCostDetails;
											travelCosts.push(travelCost);
										});
								_formData.totalAmount *= 100;
								_formData.travelCost = travelCosts;
								$.ajax({
											type : "POST",
											url : _url,
											contentType : "application/json; charset=utf-8",
											data : JSON.stringify(_formData),
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
						}, {
							text : '取消',
							handler : function() {
								$('#dlg').dialog('close');
							}
						}, {
							text : '提交',
							handler : function() {

								var _formData = $("#_form").serializeObject();
								var _url = "${contextPath}/travelCostApply/saveAndSubmit";
								var data = $('#travelCostGrid').datagrid('getData');
								var travelCosts = [];
								$(data.rows).each(function(index, item) {
											endPositionEditing($('#travelCostGrid'));
											var travelCost = {};
											var travelCostDetails = [];
											for (prop in item) {
												if (prop == 'travelDayAmount') {
													travelCost.travelDayAmount = item[prop];
												} else if (prop == 'totalAmount') {
													travelCost.totalAmount = item[prop] * 100;
												} else if (prop == 'setOutPlace') {
													travelCost.setOutPlace = item[prop];
												} else if (prop == 'destinationPlace') {
													travelCost.destinationPlace = item[prop];
												} else if (prop == 'setOutPlaceText') {
													continue;
												} else if (prop == 'destinationPlaceText') {
													continue;
												} else {
													travelCostDetails.push({
																costName : prop,
																costAmount : item[prop] * 100
															});
												}
											}
											travelCost.travelCostDetail = travelCostDetails;
											travelCosts.push(travelCost);
										});
								_formData.totalAmount *= 100;
								_formData.travelCost = travelCosts;
								$.ajax({
											type : "POST",
											url : _url,
											contentType : "application/json; charset=utf-8",
											data : JSON.stringify(_formData),
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
						}]

			});
	$('#dlg').dialog('open');
	$('#dlg').dialog('center');
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
								url : "${contextPath}/travelCostApply/delete",
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
		opts.url = "${contextPath}/travelCostApply/listPage";
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

function endPositionEditing(grid) {
	var selected = grid.datagrid('getSelected');
	var selectedIndex = grid.datagrid('getRowIndex', selected);
	if (grid.datagrid('validateRow', selectedIndex)) {
		grid.datagrid('endEdit', selectedIndex);
		return true;
	} else {
		return false;
	}
}

function onTravelCostGridEndEdit(index, row, changes) {
	var editor = $('#travelCostGrid').datagrid('getEditor', {
				index : index,
				field : 'setOutPlace'
			}).target;
	row.setOutPlaceText = editor.combobox('getText');
	editor = $('#travelCostGrid').datagrid('getEditor', {
				index : index,
				field : 'destinationPlace'
			}).target;
	row.destinationPlaceText = editor.combobox('getText');
	row.totalAmount = 0;
	for (prop in row) {
		if (prop == 'travelDayAmount') {
			continue;
		}
		if (prop == 'totalAmount') {
			continue;
		}
		if (prop.indexOf('setOutPlace') >= 0) {
			continue;
		}
		if (prop.indexOf('destinationPlace') >= 0) {
			continue;
		}
		if (row[prop]) {
			row.totalAmount += parseFloat(row[prop]);
		}
	}
	row.totalAmount *= row.travelDayAmount;
	var rows = $('#travelCostGrid').datagrid('getData').rows;
	var totalAmount = 0;
	$(rows).each(function(index, item) {
				totalAmount += parseFloat(item.totalAmount);
			});
	$('#totalAmount').numberbox('setValue', totalAmount);
}

// 全局按键事件
$.extend($.fn.datagrid.methods, {
			keyCtr : function(jq) {
				return jq.each(function() {
							var grid = $(this);
							var gridId = this.id;
							grid.datagrid('getPanel').panel('panel').attr('tabindex', 1).bind('keydown', function(e) {
										switch (e.keyCode) {
											case 46 :
												debugger;
												if (positionEditIndex) {
													return;
												}
												var selected = grid.datagrid("getSelected");
												if (selected && selected != null) {
													deletePosition();
												}
												break;
											case 13 :
												endPositionEditing(grid);
												break;
											case 27 :
												debugger;
												cancelPositionEdit(grid);
												break;
											case 38 :
												debugger;
												if (!endPositionEditing(grid)) {
													return;
												}
												var selected = grid.datagrid("getSelected");
												if (!selected) {
													return;
												}
												var selectedIndex = grid.datagrid('getRowIndex', selected);
												if (selectedIndex <= 0) {
													return;
												}
												endEditing(grid);
												grid.datagrid('selectRow', --selectedIndex);
												break;
											case 40 :
												debugger;
												if (!endPositionEditing()) {
													return;
												}
												if (grid.datagrid('getRows').length <= 0) {
													openInsertPosition();
													return;
												}
												var selected = grid.datagrid("getSelected");
												if (!selected) {
													grid.datagrid('selectRow', 0);
													return;
												}
												var selectedIndex = grid.datagrid('getRowIndex', selected);
												if (selectedIndex == grid.datagrid('getRows').length - 1) {
													openInsertPosition();
												} else {
													grid.datagrid('selectRow', ++selectedIndex);
												}
												break;
										}
									});
						});
			}
		});

$(function() {
			queryGrid();
		});