function selectFirst() {
	var data = $('#alarmType').combobox('getData');
	if (data) {
		$('#alarmType').combobox('select', data[0].code);
		$('#alarmType').combobox('initValue', data[0].value);
		$('#alarmType').combobox('setText', data[0].code);
	}
}