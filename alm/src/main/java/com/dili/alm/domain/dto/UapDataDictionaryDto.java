package com.dili.alm.domain.dto;

import com.dili.ss.domain.annotation.Like;
import com.dili.uap.sdk.domain.DataDictionary;
import com.dili.uap.sdk.domain.DataDictionaryValue;

import java.util.List;

import javax.persistence.Column;

import org.springframework.util.CollectionUtils;

public interface UapDataDictionaryDto extends DataDictionary{
	 	@Column(name = "`name`")
	    @Like(Like.BOTH)
	    String getLikeName();
	    void setLikeName(String likeName);
	    
	    public List<DataDictionaryValue> getDataDictionaryValues();

		public void setDataDictionaryValues(List<DataDictionaryValue> dataDictionaryValues);

		public default DataDictionaryValue getCodeByName(String name) {
			if (CollectionUtils.isEmpty(this.getDataDictionaryValues())) {
				return null;
			}
			for (DataDictionaryValue dataDictionaryValue : this.getDataDictionaryValues()) {
				if (dataDictionaryValue.getCode().equals(name)) {
					return dataDictionaryValue;
				}
			}
			return null;
		}
}
