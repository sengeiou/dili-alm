function setParentIframeHeight(){
    try{
        var parentIframe = parent.document.getElementById("demandForTaskSrc");
         if(window.attachEvent){
            window.attachEvent("onload", function(){
                parentIframe.height = document.documentElement.scrollHeight;
            });
            return;
        }else{
            window.onload = function(){
                parentIframe.height = document.body.scrollHeight+50;
            };
            return;                 
        }     
    }catch(e){
        throw new Error('setParentIframeHeight Error');
    }
}

function checkExecutorAmount(nval, oval) {
	var me = $(this);
	if (nval.length > 2) {
		me.combobox('unselect', nval[nval.length - 1]);
	}
}
//加载操作记录
function queryRecordGrid(code) {
       var opts = $("#recordGrid").datagrid("options");
       if (null == opts.url || "" == opts.url) {
           opts.url = "${contextPath!}/demand/getRecordList?code="+code;
       }
       $("#recordGrid").datagrid("load");
       $('#recordGrid').datagrid({

	      onLoadSuccess: function (data) { 
		       var parentIframe = parent.document.getElementById("pageType")
		       if(parentIframe!="undefined"){
		         window.parent.setIframeHeight();
		      }
	        }
       });
 }
 
 //加载文件列表
function queryFileGrid() {
       $('#fileGrid').datagrid({
	      onLoadSuccess: function (data) { 
		       var parentIframe = parent.document.getElementById("pageType")
		       if(parentIframe!="undefined"){
		         window.parent.setIframeHeight();
		      }
	        }
       });
 }