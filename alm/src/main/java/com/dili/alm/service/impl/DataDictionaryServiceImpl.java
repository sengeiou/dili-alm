package com.dili.alm.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.DataDictionaryMapper;
import com.dili.alm.dao.DataDictionaryValueMapper;
import com.dili.alm.domain.DataDictionary;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.UapDataDictionaryDto;
import com.dili.alm.rpc.DataDictionaryRpc;
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.dto.DTOUtils;

/**
 *
 * 10:41:18.
 */
@Service
public class DataDictionaryServiceImpl extends BaseServiceImpl<DataDictionary, Long> implements DataDictionaryService {

	@Autowired
	private DataDictionaryValueMapper valueMapper;
	
	@Autowired
	private DataDictionaryRpc dataDictionaryRpc;

	public DataDictionaryMapper getActualDao() {
		return (DataDictionaryMapper) getDao();
	}

	@Override
	public DataDictionaryDto findByCode(String code) {

		UapDataDictionaryDto model = dataDictionaryRpc.findByCode(code, AlmConstants.ALM_SYSTEM_CODE).getData();
		if (model == null) {
			return null;
		}
		DataDictionaryDto dataDictionaryDto=DTOUtils.newDTO(DataDictionaryDto.class);
		dataDictionaryDto.setId(model.getId());
		dataDictionaryDto.setCode(model.getCode());
		dataDictionaryDto.setName(model.getName());
		dataDictionaryDto.setNotes(model.getDescription());
		dataDictionaryDto.setCreated(model.getCreated());
		dataDictionaryDto.setModified(model.getModified());
		List<DataDictionaryValueDto> values =new ArrayList<DataDictionaryValueDto>();
		List<com.dili.uap.sdk.domain.DataDictionaryValue> DataDictionaryValues = model.getDataDictionaryValues();
		if (CollectionUtils.isNotEmpty(values)) {
			for (com.dili.uap.sdk.domain.DataDictionaryValue dataDictionaryValue : DataDictionaryValues) {
				DataDictionaryValueDto dataDictionaryValueDto=DTOUtils.newDTO(DataDictionaryValueDto.class);
				dataDictionaryValueDto.setId(dataDictionaryValue.getId());
				dataDictionaryValueDto.setCode(dataDictionaryValue.getCode());
				dataDictionaryValueDto.setDdId(dataDictionaryDto.getId());
				dataDictionaryValueDto.setParentId(dataDictionaryValue.getParentId());
				dataDictionaryValueDto.setOrderNumber(dataDictionaryValue.getOrderNumber());
				dataDictionaryValueDto.setCode(dataDictionaryValue.getName());
				dataDictionaryValueDto.setValue(dataDictionaryValue.getCode());
				dataDictionaryValueDto.setNotes(dataDictionaryValue.getDescription());
				dataDictionaryValueDto.setCreated(dataDictionaryValue.getCreated());
				dataDictionaryValueDto.setModified(dataDictionaryValue.getModified());
				values.add(dataDictionaryValueDto);
			}
			dataDictionaryDto.setValues(values);
		}
		return dataDictionaryDto;
	}

	@Override
	public int insert(DataDictionary t) {
		t.setYn(1);
		return super.insert(t);
	}

	@Override
	public int insertSelective(DataDictionary t) {
		t.setYn(1);
		return super.insertSelective(t);
	}

	@Override
	public List<DataDictionary> listDataDictionary(DataDictionary ddit) {
		com.dili.uap.sdk.domain.DataDictionary uapDate=DTOUtils.newDTO(com.dili.uap.sdk.domain.DataDictionary.class);
		if(ddit.getId()!=null) {
			uapDate.setId(ddit.getId());
		}
		if(!WebUtil.strIsEmpty(ddit.getCode())) {
			uapDate.setCode(ddit.getCode());
		}
		if(!WebUtil.strIsEmpty(ddit.getName())) {
			uapDate.setName(ddit.getName());
		}
		if(!WebUtil.strIsEmpty(ddit.getNotes())) {
			uapDate.setDescription(ddit.getNotes());
		}

		uapDate.setSystemCode(AlmConstants.ALM_SYSTEM_CODE);

		if(ddit.getCreated()!=null) {
			uapDate.setCreated(ddit.getCreated());
		}
		if(ddit.getModified()!=null) {
			uapDate.setModified(ddit.getModified());
		}
		
		List<com.dili.uap.sdk.domain.DataDictionary> data = this.dataDictionaryRpc.listDataDictionary(uapDate).getData();
		List<DataDictionary> dataDictionaryList=new ArrayList<DataDictionary>();
		for (com.dili.uap.sdk.domain.DataDictionary dataDictionary : data) {
			DataDictionary newDataDictionary=DTOUtils.newDTO(DataDictionary.class);
			newDataDictionary.setId(dataDictionary.getId());
			newDataDictionary.setCode(dataDictionary.getCode());
			newDataDictionary.setName(dataDictionary.getName());
			newDataDictionary.setNotes(dataDictionary.getDescription());
			newDataDictionary.setCreated(dataDictionary.getCreated());
			newDataDictionary.setModified(dataDictionary.getModified());
			dataDictionaryList.add(newDataDictionary);
		}
		return dataDictionaryList;
	}

}