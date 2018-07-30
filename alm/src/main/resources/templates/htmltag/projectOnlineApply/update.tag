$(function() {
			loadProject();
			var versionId = '${apply.versionId!}';
			versionId = parseInt(versionId);
			$('#versionId').combobox('initValue', versionId);
			$('#versionId').combobox('setText', '${apply.version!}');
			$('#market').combotree('loadData', markets);
			if (selectedMarkets) {
				$(selectedMarkets).each(function(index, item) {
							var node = $('#market').combotree('tree').tree('find', item.marketCode);
							$('#market').combotree('tree').tree('check', node.target);
						});
			}
		});