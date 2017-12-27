
    $(document).ready(function () {
    
       loadProjectSelect();
       loadVisionCondition();
       loadMemberCondition();
       loadProjectCondition();
       
       $("#changeIdTr").css("display","none");
        $('#_projectId').combobox({
            onChange: function (o,n) {
            	// 获取到的项目ID
            	var projectId = $('#_projectId').combobox('getValue');
            	loadVisionSelect(projectId);
            	loadMemberSelect(projectId);
            	loadTaskSelect(projectId);
            	loadChangePorjectSelect(projectId);
            }
        });

        $('#_flow').combobox({
            onSelect: function (o,n) {
            	// 获取到的项目ID
            	if(o.value==0){
            	  $("#changeIdTr").css("display","none");
            	}else{
            	  $("#changeIdTr").css("display","block");
            	  loadChangePorjectSelect();
            	}
            }
        });
    });
    
    
   function loadProjectSelect(){
    	$('#_projectId').combobox({
				url:"${contextPath!}/task/listTreeProject.json",
				valueField:'id',
				textField:'name',
				editable:false,
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
				editable:false,
				onChange: function (o,n) {
				  var versionId = $('#_versionId').combobox('getValue');
				  loadPhaseSelect(versionId);
				}
			});
			
    }
    function loadMemberSelect(id){
    	$('#_owner').combobox({
				url:"${contextPath!}/task/listTreeUserByProject.json?projectId="+id,
				valueField:'id',
				textField:'realName',
				editable:false,
				onChange: function (o,n) {

				}
			});
    }
    
    
    function loadVisionCondition(){
    	$('#versionId').combobox({
				url:"${contextPath!}/task/listTreeVersionByTeam.json",
				valueField:'id',
				textField:'version',
				editable:false,
				onChange: function (o,n) {

				}
			});
    }
   
   function loadMemberCondition(){
    	$('#owner').combobox({
				url:"${contextPath!}/task/listTreeMember.json",
				valueField:'id',
				textField:'realName',
				editable:false,
				onChange: function (o,n) {

				}
			});
    }
    
    
    function loadProjectCondition(){
    	$('#projectId').combobox({
				url:"${contextPath!}/task/listTreeProject.json",
				valueField:'id',
				textField:'name',
				editable:false,
				onChange: function (o,n) {

				}
			});
    }
    function loadPhaseSelect(id,selectId){
    	$('#_phaseId').combobox({
				url:"${contextPath!}/task/listTreePhase.json?id="+id,
				valueField:'id',
				textField:'name',
				editable:false
			});
    }
    
    function loadTaskSelect(id){
    	$('#_beforeTask').combobox({
				url:"${contextPath!}/task/listTree.json?projectId="+id,
				valueField:'id',
				textField:'name',
				editable:false,
				onChange: function (o,n) {

				}
			});
    }
    
   function loadChangePorjectSelect(id){
    	$('#_changeId').combobox({
				url:"${contextPath!}/task/listTreeProjectChange.json?projectId="+id,
				valueField:'id',
				textField:'name',
				editable:false,
				onChange: function (o,n) {

				}
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
      $("#startDateShow").datebox({disabled: true});
      $("#endDateShow").datebox({disabled: true});
      $("#_owner").combobox({disabled: true});
      $("#_flow").combobox({disabled: true});      
      $("#_type").combobox({disabled: true});
      $("#_changeId").combobox({disabled: true});
      $("#saveTask").linkbutton({disabled:true});
    }
    
    
    function canEditForTaskdetail(){
      $('#taskHourStr').textbox('textbox').attr('readonly',false);
      $('#overHourStr').textbox('textbox').attr('readonly',false);
      $('#describe').textbox('textbox').attr('readonly',false); 
    }
    
    
  function canEdit(){
  
      $('#_name').textbox('textbox').attr('readonly',false);
      $('#_describe').textbox('textbox').attr('readonly',false);
      $('#planTimeStr').textbox('textbox').attr('readonly',false);

      $("#_beforeTask").combobox({disabled: false});
      $("#_projectId").combobox({disabled: false});  
      $("#_versionId").combobox({disabled: false});
      $("#_phaseId").combobox({disabled: false});
      $("#startDateShow").datebox({disabled: false});
      $("#endDateShow").datebox({disabled: false});
      $("#_owner").combobox({disabled: false});
      $("#_flow").combobox({disabled: false}); 
      $("#_type").combobox({disabled: false});
      $("#_changeId").combobox({disabled: false});
      $("#saveTask").linkbutton({disabled:false});
    }
    
    function noEditForTaskdetail(){
      $('#taskHourStr').numberbox('textbox').attr('readonly',true);
      $('#overHourStr').numberbox('textbox').attr('readonly',true);
      $('#describe').textbox('textbox').attr('readonly',true); 
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
				noEditForTaskdetail();
			}
			canEditForTaskdetail();
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
            
            loadMemberSelect(formData._projectId);
            $('#_owner').combobox('select',formData._owner);

 			loadVisionSelect(formData._projectId);
            $('#_versionId').combobox('select',formData._versionId);
            
            loadPhaseSelect(formData._versionId);
            $('#_phaseId').combobox('select',formData._phaseId);
            
            $('#_form').form('load', {startDateShow:dateFormat_1(formData._startDate)});
            $('#_form').form('load', {endDateShow:dateFormat_1(formData._endDate)}); 
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
            
            if(formData._status==4){
                noEditForTaskdetail();
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


		//判断是否是项目经理
        function isProjectManager(){
          	  var  htmlobj=$.ajax({url:"${contextPath}/task/isProjectManger.json",async:false});
	          var str=htmlobj.responseText;
	          var obj = $.parseJSON(str);
	          return obj;
        }
        
function dateFormat_1(longTypeDate){
    var dateType = "";  
    var date = new Date();  
    date.setTime(longTypeDate);  
    dateType += date.getFullYear();   //年  
    dateType += "-" + getMonth(date); //月   
    dateType += "-" + getDay(date);   //日  
    return dateType;
} 

//返回 01-12 的月份值   
function getMonth(date){  
    var month = "";  
    month = date.getMonth() + 1; //getMonth()得到的月份是0-11  
    if(month<10){  
        month = "0" + month;  
    }  
    return month;  
}  
//返回01-30的日期  
function getDay(date){  
    var day = "";  
    day = date.getDate();  
    if(day<10){  
        day = "0" + day;  
    }  
    return day;  
}



//创建者
        function isCreater(id){
	          var  htmlobj=$.ajax({url:"${contextPath}/task/isCreater.json?id="+id,async:false});
	          var str=htmlobj.responseText;
	          var obj = $.parseJSON(str);
	          return obj;
        }