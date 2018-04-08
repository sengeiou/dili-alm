$(function() {
			var versionId = '${apply.versionId!}';
			versionId = parseInt(versionId);
			$('#versionId').combobox('initValue', versionId);
			$('#versionId').combobox('setText', '${apply.version!}');
			$($('#market').combobox('getData')).each(function(index, item) {
						$(selectedMarkets).each(function(i, val) {
									if (val == item.value) {
										$('#market').combobox('select', val);
									}
								});
					});
		});