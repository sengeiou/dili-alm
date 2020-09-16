package com.dili.alm.domain.dto;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.dili.alm.domain.DataDictionary;

public interface DataDictionaryDto extends DataDictionary {

	List<DataDictionaryValueDto> getValues();

	void setValues(List<DataDictionaryValueDto> values);

	default DataDictionaryValueDto getValueByCode(String code) {
		if (CollectionUtils.isEmpty(this.getValues())) {
			return null;
		}
		for (DataDictionaryValueDto value : this.getValues()) {
			if (value.getCode().equals(code)) {
				return value;
			}
		}
		return null;
	}

	void setValueByCode(DataDictionaryValueDto dataDictionaryValueDto);

}