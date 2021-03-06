function exportExcel() {
	var rowobj = $('#grid').datagrid('getRows');
	if (rowobj.length < 1) {
		$.messager.alert('错误', "还没有数据可以导出！");
		return;
	}
	$('#form').form('submit', {
				url : '${contextPath}/statistical/exportPorjectHours',
				success : function(data) {
					var obj = $.parseJSON(data);
					if (obj.code != 200) {
						$.messager.alert('错误', obj.result);
					}
				}
			});

}
// 清空表单
function clearForm() {
	$('#form').form('clear');
	queryGrid();
}
function onLoadSuccess(data) {
	var merges = [{
				index : 1,
				rowspan : 5
			}];
	for (var i = 0; i < merges.length; i++) {
		$(this).datagrid('mergeCells', {
					index : merges[i].index,
					field : 'attr1',
					rowspan : merges[i].rowspan
				});
	}
}

// 表格查询
function queryGrid() {
	if (!$('#form').form("validate")) {
		return;
	}
	var param = bindMetadata("grid", true);
	var formData = $("#form").serializeObject();
	formData.project = $("#projectA").val();
	$.extend(param, formData);

	easyUIDataGrid(param);
}

function initCombobox(id) {
	var value = "";
	// 加载下拉框复选框
	$('#' + id).combobox({
				url : '${contextPath}/statistical/projectList',
				panelHeight : '200px',
				method : 'post',
				valueField : 'id',
				textField : 'name',
				multiple : true,
				formatter : function(row) { // formatter方法就是实现了在每个下拉选项前面增加checkbox框的方法
					var opts = $(this).combobox('options');
					return '<input type="checkbox" class="combobox-checkbox">' + row[opts.textField]
				},
				onLoadSuccess : function() { // 下拉框数据加载成功调用
					var opts = $(this).combobox('options');
					var target = this;
					var values = $(target).combobox('getValues');// 获取选中的值的values
					$.map(values, function(value) {
								var el = opts.finder.getEl(target, value);
								el.find('input.combobox-checkbox')._propAttr('checked', true);
							})
				},
				onSelect : function(row) { // 选中一个选项时调用
					var opts = $(this).combobox('options');
					// 获取选中的值的values
					$("#" + id).val($(this).combobox('getValues'));

					// 设置选中值所对应的复选框为选中状态
					var el = opts.finder.getEl(this, row[opts.valueField]);
					el.find('input.combobox-checkbox')._propAttr('checked', true);
				},
				onUnselect : function(row) {// 不选中一个选项时调用
					var opts = $(this).combobox('options');
					// 获取选中的值的values
					$("#" + id).val($(this).combobox('getValues'));

					var el = opts.finder.getEl(this, row[opts.valueField]);
					el.find('input.combobox-checkbox')._propAttr('checked', false);
				}
			});
}

function easyUIDataGrid(param) {
	var $datagrid = {};
	var columns = new Array();
	var columns1 = new Array();
	var columns2 = new Array();

	// var param = { "medid": medid };
	$.ajax({
				url : '${contextPath}/statistical/listProjectHours',
				type : 'post',
				data : param,
				dataType : "json",
				async : false,
				success : function(returnValue) {

					var projectIds = new Array();
					columns1.push({
								"field" : "u&p",
								"title" : "人员&项目",
								"width" : 80,
								align : "center"
							});
					columns2.push({
								"field" : "userName",
								"title" : "人员",
								"width" : 80,
								align : "center"
							});

					$.each(returnValue, function(i, item) {
								columns1.push({
											"field" : "userNo",
											"title" : item.projectName + "<br/>(" + item.sumHour + "H)",
											"width" : 160,
											"align" : "center",
											"colspan" : 2
										});
								columns2.push({
											"field" : "toHours" + item.projectId,
											"title" : "常规工时",
											"width" : 80,
											align : "center",
											"rowspan" : 1,
											"formatter" : projectTaskHour
										});// 实际工时
								columns2.push({
											"field" : "ooHours" + item.projectId,
											"title" : "加班工时",
											"width" : 80,
											align : "center",
											"rowspan" : 1,
											"formatter" : projectOverHour
										});// 加班工时
								projectIds.push(item.projectId);
							});

					columns1.push({
								"field" : "heji",
								"title" : "合计",
								"width" : 240,
								align : "center",
								"colspan" : 3
							});

					columns2.push({
								"field" : "sumTaskHours",
								"title" : "常规工时",
								"width" : 80,
								align : "center",
								"rowspan" : 1
							});
					columns2.push({
								"field" : "sumOverHours",
								"title" : "加班工时",
								"width" : 80,
								align : "center",
								"rowspan" : 1
							});
					columns2.push({
								"field" : "totalHours",
								"title" : "总工时",
								"width" : 80,
								align : "center",
								"rowspan" : 1
							});

					columns.push(columns1);
					columns.push(columns2);
					$datagrid.columns = columns;

					$datagrid.url = '${contextPath}/statistical/listUserHours';
					$datagrid.queryParams = param;

					$datagrid.rowStyler = function(index, row) {
						if (index == $('#grid').datagrid('getRows').length - 1) {
							return 'background-color: #d3ecde;';
						}
					}

					$('#grid').datagrid($datagrid);

					loadInitEchartForProject(projectIds, param);
				}
			});
}

function projectTaskHour(value, row, index) {
	var projectHoursObj = row.projectHours;
	var str = "project" + this.field.slice(7) + "sumUPTaskHours";

	var aahours = 0;
	for (var i in projectHoursObj) {
		if (i == str) {
			aahours = projectHoursObj[i];
		}
	}
	return aahours;
}
function projectOverHour(value, row, index) {
	var projectHoursObj = row.projectHours;
	var str = "project" + this.field.slice(7) + "sumUPOverHours";// 截取得到id
	var aahours = 0;
	for (var i in projectHoursObj) {
		if (i == str) {
			aahours = projectHoursObj[i];
		}
	}
	return aahours;
}
function dateFormat_1() {
	var dateType = "";
	var date = new Date();

	date.setDate(date.getDate() - 7);// 当前日期减7天

	var month = date.getMonth() + 1;
	var year = date.getFullYear();
	var day = date.getDate(date);

	if (month < 10) {
		month = "0" + month;
	}

	if (day < 10) {
		day = "0" + day;
	}

	dateType = year + "-" + month + "-" + day;

	return dateType;
}

function loadInitEchartForProject(projectIds, param) {
	$.ajax({
				type : 'post',
				async : false, // 同步执行
				url : '${contextPath}/statistical/listProjectForEchar', // web.xml中注册的Servlet的url-pattern
				data : {
					projectIds : projectIds,
					startDate : param.startDate,
					endDate : param.endDate
				}, // 无参数
				dataType : 'json', // 返回数据形式为json
				traditional : true,
				success : function(result) {
					if (result) {
						// 把result(即Json数据)以参数形式放入Echarts代码中
						bindDateForEchats(result);

					}
				},
				error : function(errorMsg) {
					alert("加载数据失败");
				}
			}); // ajax
}
function bindDateForEchats(result) {
	// 处理result集
	var projectName = new Array();

	var taskHours = new Array();

	var overHours = new Array();

	var totalHours = new Array();

	var option3 = {
		tooltip : {
			trigger : 'axis',
			axisPointer : { // 坐标轴指示器，坐标轴触发有效
				type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
			}
		},
		legend : {
			data : ['常规工时', '加班工时'],
			itemGap : 100
		},
		grid : {
			left : '3%',
			right : '4%',
			bottom : '3%',
			containLabel : true
		},
		xAxis : {
			type : 'value'
		},
		yAxis : {
			type : 'category',
			data : (function() {
				var resultMapList = result;
				var res = [];
				result.forEach(function(value, index, array) {
							res.unshift(value.projectName);
						});
				return res;
			})()
		},
		dataRange : {

		},
		series : [{
					name : '加班工时',
					type : 'bar',
					stack : '总量',
					label : {
						normal : {
							show : true,
							position : 'insideRight'
						}
					},
					barMinHeight : 20,
					data : (function() {
						var resultMapList = result;
						var res = [];
						result.forEach(function(value, index, array) {
									res.unshift(value.sumUPOverHours);
								});
						return res;
					})()
				}, {
					name : '常规工时',
					type : 'bar',
					stack : '总量',
					label : {
						normal : {
							show : true,
							position : 'insideRight'
						}
					},
					data : (function() {
						var resultMapList = result;
						var res = [];
						result.forEach(function(value, index, array) {
									res.unshift(value.sumUPTaskHours);
								});
						return res;
					})()
				}, { // 总和计算
					name : '总计',
					type : 'bar',
					barGap : '-100%',
					label : {
						normal : { // 显示数据
							show : false,
							position : 'insideRight',
							formatter : '{c}',
							fontSize : 20,
							textStyle : {
								color : '#333'
							}
						}
					},
					itemStyle : {
						normal : {
							color : 'rgba(128, 128, 128, 0)'
						}
					},
					data : (function() {
						var res = [];
						result.forEach(function(value, index, array) {
									var total = parseInt(value.sumUPTaskHours) + parseInt(value.sumUPOverHours);
									res.unshift(total);
								});
						return res;
					})()
					// 总和数据
			}]
	};
	echarts.init(document.getElementById('bar2')).setOption(option3);
}

$(function() {
			// 初始化多选复选框
			initCombobox('projectA');
			easyUIDataGrid({});
			$('#startDate').textbox({
						prompt : dateFormat_1()
					});
		});