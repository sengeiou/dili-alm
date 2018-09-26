$(function() {
			$("#fileuploader").uploadFile({
						url : "${contextPath!}/files/filesUpload?projectId=" + $('#projectId').val(), // 文件上传url
						fileName : "file", // 提交到服务器的文件名
						uploadStr : '上传',
						// maxFileCount : 1, // 上传文件个数（多个时修改此处
						returnType : 'json', // 服务返回数据
						// allowedTypes : 'jpg,jpeg,png,gif', // 允许上传的文件式
						showDone : false, // 是否显示"Done"(完成)按钮
						showDelete : false, // 是否显示"Delete"(删除)按钮
						// showError : false,
						maxFileSize : 10485760, // 上传文件个数（多个时修改此处
						sizeErrorStr : '文件过大，允许上传文件最大为',// 超过最大文件限制
						showStatusAfterSuccess : false,
						onError : function(files, status, errMsg, pd) {
							$.messager.alert('错误', '上传失败！');
						},
						onSuccess : function(files, data, xhr, pd) {
							if (data.code != 200) {
								$.messager.alert('错误', data.result);
								return;
							}
							// 上传成功后的回调方法。本例中是将返回的文件名保到一个hidden类开的input中，以便后期数据处理
						}
					});
		});