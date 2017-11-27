function versionOptFormatter(value, row, index) {
	var content = '<a href="void(0);" onclick="changeVersionState();">状态变更</a>';
	content += '<a href="void(0);" onclick="editVersion();">编辑</a>';
	content += '<a href="void(0);" onclick="deleteVersion();">删除</a>';
	return content;
}

function onlineFormatter(value) {
	if (value == 0) {
		return '否';
	} else if (value == 1) {
		return '是';
	} else {
		return '';
	}
}

function openInsertVersion() {
	$('#win').dialog({
				title : '新建版本',
				width : 600,
				height : '100%',
				href : '${contextPath!}/project/version/form?projectId=' + $('#projectId').val(),
				modal : true
			});
}

function editVersion() {

}

function deleteVersion() {
}

function changeVersionState() {
}