
function print(){
			$("#dy").jqprint();
		}
 function progressFormatter(value, rowData, rowIndex) {
	var progress = value;
	var htmlstr;
	if(value!=null){
		if (value > 100) {
			progress = 100;
		}
		 htmlstr= '<div style="width: 100%; height:20px;border: 1px solid #299a58;"><div style="width:' + progress + '%; height:20px; background-color: #299a58;"><span>' + value
				+ '%</span></div></div>';
	}
	return htmlstr;
}
 //表格查询
        function queryGrid(n) {
            var opts = $("#grid").datagrid("options");
           	if(n!=0){
           		$('#form').form('clear');
           		opts.url = "${contextPath}/statistical/ProjectProgressList?flat="+n;
           	}else{
               	opts.url = "${contextPath}/statistical/ProjectProgressList?&ids="+$("#ids").val();
            }
            if(!$('#form').form("validate")){
                return;
            }
            var param = bindMetadata("grid", true);
            var formData = $("#form").serializeObject();
            $.extend(param, formData);
            $("#grid").datagrid("load", param);
           
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




