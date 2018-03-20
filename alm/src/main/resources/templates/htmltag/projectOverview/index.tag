 //表格查询
        function queryGrid(n) {
        	var nums=[];
        	var nums1=[];
            var opts = $("#grid").datagrid("options");
            var opts1 = $("#grid1").datagrid("options");
           	if(n!=0){
           		$('#form').form('clear');
           		opts.url = "${contextPath}/statistical/ProjectOverviewlist?flat="+n;
           		opts1.url = "${contextPath}/statistical/ProjectOverviewTasklist?flat="+n;
           	}else{
               	opts.url = "${contextPath}/statistical/ProjectOverviewlist";
               	opts1.url = "${contextPath}/statistical/ProjectOverviewTasklist";
            }
            if(!$('#form').form("validate")){
                return;
            }
            var param = bindMetadata("grid", true);
            var param1 = bindMetadata("grid1", true);
            var formData = $("#form").serializeObject();
            $.extend(param, formData);
            $.extend(param1, formData);
            $("#grid").datagrid("load", param);
            $("#grid1").datagrid("load", param1);
            $.ajax({
	         type : "post",
	         async : true,           
	         url : opts.url,   
	         data :{"startTime":$("#startTime").val(),"endTime":$("#endTime").val()},
	         dataType : "json",       
	         success : function(result) {  
	         		var res=result.rows;
	         		for(var i=0;i<res.length;i++){
	         			var enti={"name": res[i].type,"type":'bar',"label": labelOption,"data": [res[i].notStartCount,res[i].ongoingConut,res[i].completeCount,res[i].suspendedCount,res[i].shutCount]};   
                       nums.push(enti);    
                     }    
					echarts.init(document.getElementById('project')).hideLoading();   
                    echarts.init(document.getElementById('project')).setOption({        
                        series: nums
                    });
	         }
                  
	   		})
	   		 $.ajax({
	         type : "post",
	         async : true,           
	         url : opts1.url,   
	         data :{"startTime":$("#startTime").val(),"endTime":$("#endTime").val()},
	         dataType : "json",       
	         success : function(result) {  
	         		
	    			for(var i=0;i<result.length;i++){
	         			var enti={"name": result[i].taskState,"value": result[i].stateCount};   
                       nums1.push(enti);    
                     } 
                     console.log(nums1);
					echarts.init(document.getElementById('task')).hideLoading();   
                    echarts.init(document.getElementById('task')).setOption({ 
     					series: [{
                        	data: nums1
                         }]
                    });
	         }
                  
	   		})
        }
    	
        //清空表单
	    function clearForm() {
	        $('#form').form('clear');
	        queryGrid(0);
	    }

        //表格表头右键菜单
        function headerContextMenu(e, field){
            e.preventDefault();
            if (!cmenu){
                createColumnMenu("grid");
            }
            cmenu.menu('show', {
                left:e.pageX,
                top:e.pageY
            });
        }

        $.messager.progress({
            title:"提示",
            msg:"加载中,请稍候...",
            value : '10',
            text: '{value}%',
            interval:200
        });
        $.parser.onComplete = function(){
            $.messager.progress("close");
        }
        //全局按键事件
        function getKey(e){
            e = e || window.event;
            var keycode = e.which ? e.which : e.keyCode;
            if(keycode == 46){ //如果按下删除键
                var selected = $("#grid").datagrid("getSelected");
                if(selected && selected!= null){
                    del();
                }
            }
        }

        /**
         * 绑定页面回车事件，以及初始化页面时的光标定位
         * @formId
         *          表单ID
         * @elementName
         *          光标定位在指点表单元素的name属性的值
         * @submitFun
         *          表单提交需执行的任务
         */
        $(function () {
            //bindFormEvent("form", "created", queryGrid);
            if (document.addEventListener) {
                document.addEventListener("keyup",getKey,false);
            } else if (document.attachEvent) {
                document.attachEvent("onkeyup",getKey);
            } else {
                document.onkeyup = getKey;
            }
            queryGrid(0) 
        })