package com.dili.alm.rpc;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.alm.domain.dto.UapDataDictionaryDto;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.retrofitful.annotation.POST;
import com.dili.ss.retrofitful.annotation.Restful;
import com.dili.ss.retrofitful.annotation.VOBody;
import com.dili.ss.retrofitful.annotation.VOField;
import com.dili.uap.sdk.domain.DataDictionary;
import com.dili.uap.sdk.domain.DataDictionaryValue;


@Restful("http://uap.diligrp.com/")
public interface DataDictionaryRpc {
	
	@POST("/dataDictionaryApi/findByCode.api") 
	BaseOutput<UapDataDictionaryDto> findByCode(@VOField("code") String code,@VOField("systemCode") String systemCode);
	
	
	@POST("/dataDictionaryApi/listDataDictionaryValue.api") 
	BaseOutput<List<DataDictionaryValue>> listDataDictionaryValue(@VOBody DataDictionaryValue dataDictionaryValue);
	
	@POST("/dataDictionaryApi/listDataDictionary.api") 
	BaseOutput<List<DataDictionary>> listDataDictionary(@VOBody DataDictionary dataDictionary);
	/**
	 * 添加数据字典值
	 * @param dataDictionaryValue
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/insertDataDictionaryDto.api", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Object> insertDataDictionaryDto(@VOBody UapDataDictionaryDto dataDictionaryDto);
        
}
