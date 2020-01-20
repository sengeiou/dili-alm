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

$(function () {
    //加载父元素高度
    setParentIframeHeight();
})