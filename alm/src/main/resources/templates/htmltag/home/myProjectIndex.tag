
    //进度条处理
    function progressFormatter(value, rowData, rowIndex) {
        var progress = value;
        if (value > 100) {
            progress = 100;
        }
        var htmlstr = '<div style="width: 100px; height:20px;border: 1px solid #299a58;"><div style="width:' + progress + 'px; height:20px; background-color: #299a58;"><span>' + value + '%</span></div></div>';
        return htmlstr;
    }
        //表格查询
        function queryGrid() {
            var opts = $("#grid").datagrid("options");
            if (null == opts.url || "" == opts.url) {
                opts.url = "${contextPath}/home/listPage";
            }
            if(!$('#form').form("validate")){
                return;
            }
            var param = bindMetadata("grid", true);
            var formData = $("#form").serializeObject();
            $.extend(param, formData);
            $("#grid").datagrid("load", param);
            $('#grid').datagrid({
                view: myview,
                emptyMsg: '当前没有任何项目'
          });
        }

        var myview = $.extend({},$.fn.datagrid.defaults.view,{
            onAfterRender:function(target){
                $.fn.datagrid.defaults.view.onAfterRender.call(this,target);
                var opts = $(target).datagrid('options');
                var vc = $(target).datagrid('getPanel').children('div.datagrid-view');
                vc.children('div.datagrid-empty').remove();
                if (!$(target).datagrid('getRows').length){
                    var d = $('<div class="datagrid-empty"></div>').html(opts.emptyMsg || 'no records').appendTo(vc);
                    d.css({
                        position:'absolute',
                        left:0,
                        top:50,
                        width:'100%',
                        textAlign:'center'
                    });
                }
            }
         });

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

      function formatOper(val,row,index){ 
      		
		    if (!row.manager) {
				return '';
			} 
        	return '<a href="javascript:void(0)" onclick="formatOperSrc('+ row.id + ')">管理</a>';
        }        
        
        //项目名称
        function formatprojectName(val,row,index){  
        	return  '<span class="opt" style="padding:5px;"><a href="javascript:void(0)" onclick="projectNameSrc('+ row.id + ')" target="_blank">'+val+'</a></span>';        
        }
 		function projectNameSrc(rid){
 			$("#myProject", parent.document).attr("src","${contextPath!}/project/detail.html?id="+ rid+"&backUrl=${contextPath!}/home/myProjectIndex");
 		}
 		function formatOperSrc(rid){
 			$("#myProject", parent.document).attr("src","${contextPath!}/project/detail.html?id="+ rid+"&editable=true&backUrl=${contextPath!}/home/myProjectIndex");
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
            if (document.addEventListener) {
                document.addEventListener("keyup",getKey,false);
            } else if (document.attachEvent) {
                document.attachEvent("onkeyup",getKey);
            } else {
                document.onkeyup = getKey;
            }
            
            queryGrid();
        })
