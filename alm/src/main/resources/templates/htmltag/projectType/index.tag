
//打印列表
		
		function btnPrintClick(){
		    var imgBox = $('#img_box');
		    var chartBox = $('#main');
		    if (imgBox.length <= 0) {
		        chartBox.after('<div id="img_box"></div>');
		        imgBox = $('#img_box');
		    }
		    imgBox.html('< img src="' + myChart.getDataURL() + '"/>').css('display','block');
		    chartBox.css('display','none');
		    var img = imgBox.find('img');
		    var imgWidth = img.width();
		    var showWidth = 1000; 
		    if (imgWidth > showWidth) { 
		        var imgNewHeight = img.height() / (imgWidth / showWidth);
		        img.css({'width': showWidth + 'px', 'height': imgNewHeight + 'px'});
		    }
		
		    var imgBox2 = $('#img_box2');
		    var chartBox2 = $('#main2');
		    if (imgBox2.length <= 0) {
		        chartBox2.after('<div id="img_box2"></div>');
		        imgBox2 = $('#img_box2');
		    }
		
		    imgBox2.html('< img src="' + myChart2.getDataURL() + '"/>').css('display','block');
		        chartBox2.css('display','none');
		    var img2 = imgBox2.find('img');
		    var img2Width = img2.width();
		    var show2Width = 1000; 
		    if (img2Width > show2Width) { 
		        var img2NewHeight = img2.height() / (img2Width / show2Width);
		        img2.css({'width': show2Width + 'px', 'height': img2NewHeight + 'px'});
		    }
		
		    // 打印
		    $("#dy").jqprint();
		
		    // 执行打印后再切换回来
		    // 显示echart图chart-box
		    chartBox.css('display','block');
		    chartBox2.css('display','block');
		    // 隐藏图片img-box
		    imgBox.css('display','none');
		    imgBox2.css('display','none');
		}


 function progressFormatter(value, rowData, rowIndex) {
	var progress = value;
	var htmlstr;
	if(value!=null){
		var values = parseInt(value);
		if (values > 100) {
			progress = 100;
		}
		 htmlstr= '<div style="width: 100%; height:20px;border: 1px solid #299a58;"><div style="width:' + progress + '; height:20px; background-color: #299a58;"><span>' + value
				+ '</span></div></div>';
	}
	return htmlstr;
}
 //表格查询
        function queryGrid(n) {
        	var nums=[];
        	var names=[];
            var opts = $("#grid").treegrid("options");
           	if(n!=0){
           		$('#form').form('clear');
           		opts.url = "${contextPath}/statistical/projecTypetList?flat="+n;
           	}else{
               	opts.url = "${contextPath}/statistical/projecTypetList";
            }

            if(!$('#form').form("validate")){
                return;
            }
            var param = bindMetadata("grid", true);
            var formData = $("#form").serializeObject();
            $.extend(param, formData);
            $("#grid").treegrid("load", param);
            
	   		 $.ajax({
	         type : "post",
	         async : true,           
	         url : opts.url,   
	         data :{"startTime":$("#startTime").val(),"endTime":$("#endTime").val()},
	         dataType : "json",       
	         success : function(result) {  
	         		
	    			for(var i=0;i<result.length;i++){
	         			var enti={"name": result[i].type,"value": result[i].typeCount}; 
                       nums.push(enti);  
                       names.push(result[i].type);  
                     } 
					echarts.init(document.getElementById('task')).hideLoading();   
                    echarts.init(document.getElementById('task')).setOption({ 
                    legend: {
			            data: names
			        },
     					series: [{
                        	data: nums
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
                var selected = $("#grid").treegrid("getSelected");
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