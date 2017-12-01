$(function() {
			$("#fileuploader").uploadFile({
						url : "${contextPath!}/files/filesUpload", // 文件上传url
						fileName : "file", // 提交到服务器的文件名
						uploadStr : '上传',
						// maxFileCount : 1, // 上传文件个数（多个时修改此处
						returnType : 'json', // 服务返回数据
						// allowedTypes : 'jpg,jpeg,png,gif', // 允许上传的文件式
						showDone : false, // 是否显示"Done"(完成)按钮
						showDelete : false, // 是否显示"Delete"(删除)按钮
						showError : false,
						showStatusAfterSuccess : false,
						deleteCallback : function(data, pd) {
							// 文件删除时的回调方法。
							// 如：以下ajax方法为调用服务器端删除方法删除服务器端的文件
							$.ajax({
										cache : false,
										url : "file/upload",
										type : "DELETE",
										dataType : "json",
										data : {
											file : data.url
										},
										success : function(data) {
											if (data.code === 0) {
												pd.statusbar.hide(); // 删除成功后隐藏进度条等
												$('#image').val('');
											} else {
												console.log(data.message); // 打印服务器返回的错误信息
											}
										}
									});
						},
						onError : function(files, status, errMsg, pd) {
							$.messager.alert('错误', '上传失败！');
						},
						onSuccess : function(files, data, xhr, pd) {
							// 上传成功后的回调方法。本例中是将返回的文件名保到一个hidden类开的input中，以便后期数据处理
							$('#versionForm').append('<input type="hidden" name="fileIds" value="' + data.data[0].id + '">');
							$('#versionFileGrid').datagrid('appendRow', data.data[0]);
						}
					});
		});