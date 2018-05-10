function onProvinceLoadSuccess() {
	var data = $('#province').combobox('getData')[0];
	$('#province').combobox('select', data.id);
}

function onProvinceChange(nval, oval) {
	$('#city').combobox('reload', '${contextPath!}/area/list?parentId=' + nval);

}

function onCityLoadSuccess() {
	var data = $('#city').combobox('getData')[0];
	$('#city').combobox('select', data.id);
	$('#city').combobox('enable');
}

$(function() {
			$('#travelCostGrid').datagrid('keyCtr');
		});