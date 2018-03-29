$(function() {
			var versionId = '${apply.versionId!}';
			versionId = parseInt(versionId);
			$('#versionId').combobox('initValue', versionId);
			$('#versionId').combobox('setText', '${apply.version!}');
		});