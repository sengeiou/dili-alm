
    $(document).ready(function () {
       $("#changeIdTr").css("display","none");
        $('#_projectId').combobox({
            onSelect: function (o,n) {
            	// 获取到的项目ID
            	loadVisionSelect(o.value);
            }
        });

        $('#_flow').combobox({
            onSelect: function (o,n) {
            	// 获取到的项目ID
            	if(o.value==0){
            	  $("#changeIdTr").css("display","block");
            	}else{
            	  $("#changeIdTr").css("display","none");
            	}
            }
        });
    });
    
    function loadVisionSelect(id){
    	$('#_versionId').combobox({
				url:"${contextPath!}/task/listTreeVersion.json?id="+id,
				valueField:'id',
				textField:'version',
				editable:true,
				onSelect: function (o,n) {
            	// 获取到的项目ID
            	loadPhaseSelect(o.value);
            	
            }
			});
    }
    
    
    function loadPhaseSelect(id){
    	$('#_phaseId').combobox({
				url:"${contextPath!}/task/listTreePhase.json?id=1",
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
      $("#_startDate").combobox({disabled: true});
      $("#_endDate").combobox({disabled: true});
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
      $("#_startDate").combobox({disabled: false});
      $("#_endDate").combobox({disabled: false});
      $("#_owner").combobox({disabled: false});
      $("#_flow").combobox({disabled: false}); 
      $("#_type").combobox({disabled: false});
      $("#_changeId").combobox({disabled: false});
    }
    
            //打开执行任务窗口
        function openUpdateDetail(row){
        	//noEdit();
        	$("#task_detail").hide();
        	$("#dialog_toolbar").hide();
        	$("#dialog_toolbar_detail").show();
			var selected=$("#grid").datagrid('getData').rows[row];
			$("#task_detail").show();
			
            
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
/*             $('#_progress').progressbar({
 			    value: formData._progress;
 			}); */
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
        }