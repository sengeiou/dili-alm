$
		.extend(
				$.fn.datagrid.methods,
				{
					keyCtr : function(jq) {
						return jq
								.each(function() {
									var grid = $(this);
									var gridId = this.id;
									grid
											.datagrid('getPanel')
											.panel('panel')
											.attr('tabindex', 1)
											.bind(
													'keydown',
													function(e) {
														switch (e.keyCode) {
														case 46:
															if (isEditing(gridId))
																return;
															var selected = grid
																	.datagrid("getSelected");
															if (selected
																	&& selected != null) {
																if (deleting) {
																	return;
																}
																del(gridId);
															}
															break;
														case 13:
															endEditing(gridId);
															break;
														case 27:
															cancelEdit(gridId);
															break;
														case 38:
															if (!endEditing(gridId)) {
																return;
															}
															var selected = grid
																	.datagrid("getSelected");
															if (!selected) {
																return;
															}
															var selectedIndex = grid
																	.datagrid(
																			'getRowIndex',
																			selected);
															if (selectedIndex <= 0) {
																return;
															}
															endEditing(gridId);
															grid
																	.datagrid(
																			'selectRow',
																			--selectedIndex);
															break;
														case 40:
															if (!endEditing(gridId)) {
																return;
															}
															if (grid
																	.datagrid('getRows').length <= 0) {
																openInsert(gridId);
																return;
															}
															var selected = grid
																	.datagrid("getSelected");
															if (!selected) {
																grid
																		.datagrid(
																				'selectRow',
																				0);
																return;
															}
															var selectedIndex = grid
																	.datagrid(
																			'getRowIndex',
																			selected);
															if (selectedIndex == grid
																	.datagrid('getRows').length - 1) {
																openInsert(gridId);
															} else {
																grid
																		.datagrid(
																				'selectRow',
																				++selectedIndex);
															}
															break;
														}
													});
								});
					}
				});