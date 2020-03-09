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