function print() {
	$("#gantt").jqprint();
}

function loadData() {
	$('#form').form('submit', {
				url : "${contextPath!}/projectActionRecord/gantt.json",
				success : function(res) {
					var data = $.parseJSON(res);
					if (data.code == "200") {
						$('#gantt').gantt({
									source : data.data,
									itemsPerPage : 15,
									months : ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
									dow : ["S", "M", "T", "W", "T", "F", "S"],
									navigate : 'scroll',
									waitText : '数据加载中......',
									scale : "days",
									minScale : "days",
									maxScale : "months",
									onRender : function() {
									}
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

function exportExcel() {
	$('#form').form('submit', {
				url : '${contextPath}/projectActionRecord/export',
				success : function(data) {
					var obj = $.parseJSON(data);
					if (obj.code != 200) {
						$.messager.alert('错误', obj.result);
					}
				}
			});
}

function initCombobox(id) {
	var value = "";
	$('#' + id).combobox({
				url : '${contextPath!}/projectActionRecord/actions.json',
				method : 'post',
				panelHeight : 200,
				valueField : 'code',
				textField : 'name',
				multiple : true,
				formatter : function(row) {
					var opts = $(this).combobox('options');
					return '<input type="checkbox" class="combobox-checkbox">' + row[opts.textField]
				},
				onLoadSuccess : function() {
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
					var name = $("#" + id).val($(this).combobox('getValues'));
					// 当点击全选时，则勾中所有的选项
					if (name = "全选") {
						var a = $("#" + id).combobox('getData');
						for (var i = 0; i < a.length; i++) {
							var el = opts.finder.getEl(this, a[i].text);
							el.find('input.combobox-checkbox')._propAttr('checked', true);
						}
					}
					// 设置选中值所对应的复选框为选中状态
					var el = opts.finder.getEl(this, row[opts.valueField]);
					el.find('input.combobox-checkbox')._propAttr('checked', true);
				},
				onUnselect : function(row) {// 不选中一个选项时调用
					var opts = $(this).combobox('options');
					// 获取选中的值的values
					$("#" + id).val($(this).combobox('getValues'));
					// 当取消全选勾中时，则取消所有的勾选
					if ($(this).combobox('getValues') == "全选") {
						var a = $("#" + id).combobox('getData');
						for (var i = 0; i < a.length; i++) {
							var el = opts.finder.getEl(this, a[i].text);
							el.find('input.combobox-checkbox')._propAttr('checked', false);
						}
					}
					var el = opts.finder.getEl(this, row[opts.valueField]);
					el.find('input.combobox-checkbox')._propAttr('checked', false);
				}
			});

}

$(function() {
			initCombobox('actionCode');
		});