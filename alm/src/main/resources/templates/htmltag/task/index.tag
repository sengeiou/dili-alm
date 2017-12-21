
    $(document).ready(function () {
       loadProjectSelect();
       $("#changeIdTr").css("display","none");
        $('#_projectId').combobox({
            onChange: function (o,n) {
            	// 获取到的项目ID
            	var projectId = $('#_projectId').combobox('getValue');
            	loadVisionSelect(projectId);
            }
        });

        $('#_flow').combobox({
            onSelect: function (o,n) {
            	// 获取到的项目ID
            	if(o.value==0){
            	  $("#changeIdTr").css("display","none");
            	}else{
            	  $("#changeIdTr").css("display","block");
            	}
            }
        });
    });
    
   function loadProjectSelect(){
    	$('#_projectId').combobox({
				url:"${contextPath!}/task/listTreeProject.json",
				valueField:'id',
				textField:'name',
				editable:true,
				onChange: function (o,n) {
				  var versionId = $('#_versionId').combobox('getValue');
				  loadPhaseSelect(versionId); 
				}
			});
			
    }
    function loadVisionSelect(id){
    	$('#_versionId').combobox({
				url:"${contextPath!}/task/listTreeVersion.json?id="+id,
				valueField:'id',
				textField:'version',
				editable:true,
				onChange: function (o,n) {
				  var versionId = $('#_versionId').combobox('getValue');
				  loadPhaseSelect(versionId);
				}
			});
			
    }
    
    
    function loadPhaseSelect(id,selectId){
    	$('#_phaseId').combobox({
				url:"${contextPath!}/task/listTreePhase.json?id="+id,
				valueField:'id',
				textField:'name',
				editable:true
			});
    }
    
    
    function noEdit(){
      $('#_name').textbox('textbox').attr('readonly',true);
      $('#_describe').textbox('textbox').attr('readonly',true);
      $('#planTimeStr').textbox('textbox').attr('readonly',true);

      $("#_beforeTask").combobox({disabled: true});
      $("#_projectId").combobox({disabled: true});  
      $("#_versionId").combobox({disabled: true});
      $("#_phaseId").combobox({disabled: true});
      $("#_startDate").datebox({disabled: true});
      $("#_endDate").datebox({disabled: true});
      $("#_owner").combobox({disabled: true});
      $("#_flow").combobox({disabled: true}); 
      $("#_type").combobox({disabled: true});
      $("#_changeId").combobox({disabled: true});
    }
    
  function canEdit(){
  
      $('#_name').textbox('textbox').attr('readonly',false);
      $('#_describe').textbox('textbox').attr('readonly',false);
      $('#planTimeStr').textbox('textbox').attr('readonly',false);

      $("#_beforeTask").combobox({disabled: false});
      $("#_projectId").combobox({disabled: false});  
      $("#_versionId").combobox({disabled: false});
      $("#_phaseId").combobox({disabled: false});
      $("#_startDate").datebox({disabled: false});
      $("#_endDate").datebox({disabled: false});
      $("#_owner").combobox({disabled: false});
      $("#_flow").combobox({disabled: false}); 
      $("#_type").combobox({disabled: false});
      $("#_changeId").combobox({disabled: false});
    }
    
            //打开执行任务窗口
        function openUpdateDetail(row){
        	noEdit();
        	
        	$("#task_detail").hide();
        	$("#dialog_toolbar").hide();
        	$("#dialog_toolbar_detail").show();
			var selected=$("#grid").datagrid('getData').rows[row];
			$("#task_detail").show();
			
			if(!isOwenr(selected.id)){
				$.messager.alert('警告','只有任务所有者才可以编辑！');
                return;
			}
			
            $('#dlg').dialog('open');
            $('#dlg').dialog('center');

            $('#_form').form({editable:true});
            formFocus("_form", "_name");
            var formData = $.extend({},selected);
            formData = addKeyStartWith(getOriginalData(formData),"_");
            $('#_form').form('load', formData);
            $('#_form').form('load',{planTimeStr:formData._planTime});
            if(selected.flow){ $("#changeIdTr").css("display","none");}
            else{ $("#changeIdTr").css("display","block");}

 			loadVisionSelect(formData._projectId);
            $('#_versionId').combobox('select',formData._versionId);
            
            loadPhaseSelect(formData._versionId);
            $('#_phaseId').combobox('select',formData._phaseId);
            
            
            if(formData._status==0){
            	$("#task_detail").hide();
            	$("#doTask").show();
            	$("#pauseTask").hide();
            	$("#updateDetail").hide();
            }
            if(formData._status==2){
            	$("#task_detail").show();
            	$("#doTask").show();
            	$("#pauseTask").hide();
            	$("#updateDetail").hide();
            }
            
            if(formData._status==1){
            	$('#_progressShow').progressbar({
            	    value: parseInt(formData._progress)
            	});
            	
            	$("#task_detail").show();
            	$("#doTask").hide();
            	$("#pauseTask").show();
            	$("#updateDetail").show();
            	$('#detail_form').form('clear');
            	getDetailInfo(formData._id);
            }
            
            if(formData._status==3){
            	$('#_progressShow').progressbar({
            	    value: parseInt(formData._progress)
            	});
            	$("#task_detail").show();
            	$("#doTask").hide();
            	$("#pauseTask").hide();
            	$("#updateDetail").hide();
            	$('#detail_form').form('clear');
            	getDetailInfo(formData._id);
            }
            $('#detail_form').form('load',{overHourStr:0});
            $('#detail_form').form('load',{taskHourStr:0});
        }
        
        
        function isOwenr(id){
	          var  htmlobj=$.ajax({url:"${contextPath}/task/isOwner.json?id="+id,async:false});
	          var str=htmlobj.responseText;
	          var obj = $.parseJSON(str);
	          return obj;
        }
        
        
       function saveTaskDetail(){
       
        	var planTimeStr=$("#planTimeStr").val();
        	var formDate=$("#detail_form").serialize();
            if($('#overHourStr').textbox('getValue')==""){$('#detail_form').form('load',{overHourStr:0});}
            var planTimeStr=$("#planTimeStr").val();

            if(!$('#detail_form').form("validate")){
                return;
            }
            

             $.ajax({
                type: "POST",
                url: "${contextPath}/task/updateTaskDetails?planTimeStr="+planTimeStr,
                data: formDate,
                processData:true,
                dataType: "json",
                async : true,
                success: function (data) {
                    if(data.code=="200"){
                        $("#grid").datagrid("reload");
                        $('#dlg').dialog('close');
                        //LogUtils.saveLog("添加执行任务时间：" + data.result, function () {});
                    }else{
                        $.messager.alert('错误',data.result);
                    }
                },
                error: function(){
                    $.messager.alert('错误','远程访问失败');
                }
            }); 
        }
        
        
        //工时填写校验
        $.extend($.fn.validatebox.defaults.rules, {
            taskHours:{
                validator: function (value) {
                    return value <= 8;
                },
                message: '当日所填任务工时只能是8小时！'
        }
        })
