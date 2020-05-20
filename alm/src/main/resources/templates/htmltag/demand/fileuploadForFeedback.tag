//反馈意见上传控件方法加载 上传文件，显示文件
function loadUploadFile(){
	$("#fileuploaderForFD").uploadFile({
		url :"${contextPath!}/files/filesUpload", // 文件上传url                 //文件上传url
	    language: 'zh', //设置语言
	    fileName : "file",             //提交到服务器的文件名
	    uploadStr: '上传附件',
	    maxFileCount: 1,                //上传文件个数（多个时修改此处
	    returnType: 'json',              //服务返回数据
	    allowedTypes: 'doc,xls,ppt,pdf,txt,dwg,exb,dps,wps,et,rar,zip,xmind,JPG,GIF,png,bmp',  //允许上传的文件式
	    extErrorStr : '文件格式错误！允许上传格式：',
	    dragDropStr:'',
	    showDone: false,                     //是否显示"Done"(完成)按钮
	    showDelete: false,                  //是否显示"Delete"(删除)按钮
	    deleteStr: '删除',
		abortStr: '上传中',
		showStatusAfterSuccess : false,
	    onSuccess: function(files,data,xhr,pd)
	    {
	    	// 上传成功后的回调方法。本例中是将返回的文件名保到一个hidden类开的input中，以便后期数据处理
			$('#form').append('<input type="hidden"  name="documentUrl" value="' + data.data[0].id + '">');
			$('#filePath').append('<p><b>已上传方案附件:</b></p>');
			$('#filePath').append('<span style="margin: 40px;font-size: 14px; padding-top:20px;">' +data.data[0].name+'<a href="javascript:delFile(' + data.data[0].id + ')" style="margin-left: 40px;">删除</a><a href="delFile(id)" style="margin-left: 10px;">查看</a></span>');
	    }
	}); 
}

function delFile(id){
				$.ajax({
						type : "POST",
						url : '${contextPath!}/files/delete',
						data : {
							id : id
						},
						success : function(dataresult) {
							if (dataresult.code == 200) {
			                    removeHidden();
							} else {
								$.messager.alert('错误', dataresult.result);
							}

						},
						error : function() {
							$.messager.alert('错误', '远程访问失败');
						}
					});

}
 
 
function removeHidden(data,pd){

	 var parent=document.getElementById("form");
     var last_son=parent.lastElementChild;
     parent.removeChild(last_son);
     
     
     var el = document.getElementById('filePath'); 
	 var childs = el.childNodes; 
	 for(var i = childs .length - 1; i >= 0; i--) {
	   el.removeChild(childs[i]);
	 }
	 //重新加载控件
     loadUploadFile()
 }
 
 
function showFDFile(id){

				$.ajax({
						type : "POST",
						url : '${contextPath!}/demand/files/fdFile',
						data : {
							id : id
						},
						success : function(dataresult) {
						    console.log(dataresult);
							$('#filePath').append('<p><b>已上传方案附件:</b></p>');
 							$('#filePath').append('<span style="margin: 40px;font-size: 14px; padding-top:20px;">' +dataresult.name+'<a href="javascript:downloadFile('+dataresult.id+')" style="margin-left: 10px;">下载</a></span>');

						},
						error : function() {
							$.messager.alert('错误', '远程访问失败');
						}
					});

}
