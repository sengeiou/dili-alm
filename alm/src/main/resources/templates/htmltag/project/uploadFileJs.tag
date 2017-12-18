function uploadFileOptFormatter(value, row, index) {
	return '<a href="javascript:void(0);" onclick="delUploadFile(' + row.id + ');">删除</a>';
}

function delUploadFile(id) {
	$.messager.confirm('提示', '确定要删除该文档？', function(flag) {
				if (!flag) {
					return false;
				}
				$.ajax({
							type : "POST",
							url : '${contextPath!}/files/delete',
							data : {
								id : id
							},
							success : function(data) {
								if (data.code == 200) {
									var selected = $('#uploadFileGrid').datagrid('getSelected');
									var index = $('#uploadFileGrid').datagrid('getRowIndex', selected);
									$('#uploadFileGrid').datagrid('deleteRow', index);
									$('input[name=fileIds][value=' + id + ']').remove();
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

function onVersionIdChange(newVal, oldVal) {
	$('#phaseId').combobox('reload', '${contextPath!}/project/phase/list?versionId=' + newVal);
	$('#phaseId').combobox('enable');
}

function showOrHideSelectMember(nv, ov) {
	if (nv == 0) {
		$('#receiver').textbox('disable');
	}
	if (nv == 1) {
		$('#receiver').textbox('enable');
	}
}

$(function() {
			$('#receiver').textbox('addClearBtn', 'icon-clear');
			$("#fileuploader").uploadFile({
						url : "${contextPath!}/files/filesUpload", // 文件上传url
						fileName : "file", // 提交到服务器的文件名
						uploadStr : '上传',
						// maxFileCount : 1, // 上传文件个数（多个时修改此处
						returnType : 'json', // 服务返回数据
						maxFileSize : 10 * 1024 * 1024,
						// allowedTypes : 'jpg,jpeg,png,gif', // 允许上传的文件式
						showDone : false, // 是否显示"Done"(完成)按钮
						showDelete : false, // 是否显示"Delete"(删除)按钮
						showError : false,
						showStatusAfterSuccess : false,
						onError : function(files, status, errMsg, pd) {
							$.messager.alert('错误', '上传失败！');
						},
						onSuccess : function(files, data, xhr, pd) {
							// 上传成功后的回调方法。本例中是将返回的文件名保到一个hidden类开的input中，以便后期数据处理
							$('#uploadForm').append('<input type="hidden" name="fileIds" value="' + data.data[0].id + '">');
							var row = data.data[0];
							var date = new Date();
							date.setTime(row.created);
							row.created = date.Format('yyyy-MM-dd HH:mm:ss');
							$('#uploadFileGrid').datagrid('appendRow', row);
						}
					});
		});