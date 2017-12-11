
    $(document).ready(function () {
        $('#_projectId').combobox({
            onSelect: function (o,n) {
            	// 获取到的项目ID
            	loadVisionSelect(o.value);
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