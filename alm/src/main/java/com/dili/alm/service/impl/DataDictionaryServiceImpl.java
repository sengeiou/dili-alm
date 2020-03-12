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
import com.dili.alm.service.DataDictionaryService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.sdk.domain.dto.UapDataDictionaryDto;
import com.dili.uap.sdk.rpc.DataDictionaryRpc;

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
		DataDictionaryDto dataDictionaryDto = DTOUtils.newDTO(DataDictionaryDto.class);
		dataDictionaryDto.setId(model.getId());
		dataDictionaryDto.setCode(model.getCode());
		dataDictionaryDto.setName(model.getName());
		if (!WebUtil.strIsEmpty(model.getDescription())) {
			dataDictionaryDto.setNotes(model.getDescription());
		}
		if (model.getCreated() != null) {
			dataDictionaryDto.setCreated(model.getCreated());
		}
		if (model.getModified() != null) {
			dataDictionaryDto.setModified(model.getModified());
		}
		List<DataDictionaryValueDto> values = new ArrayList<DataDictionaryValueDto>();
		List<com.dili.uap.sdk.domain.DataDictionaryValue> DataDictionaryValues = model.getDataDictionaryValues();
		if (CollectionUtils.isNotEmpty(DataDictionaryValues)) {
			for (com.dili.uap.sdk.domain.DataDictionaryValue dataDictionaryValue : DataDictionaryValues) {
				DataDictionaryValueDto dataDictionaryValueDto = DTOUtils.newDTO(DataDictionaryValueDto.class);
				dataDictionaryValueDto.setId(dataDictionaryValue.getId());
				dataDictionaryValueDto.setCode(dataDictionaryValue.getCode());
				dataDictionaryValueDto.setDdId(dataDictionaryDto.getId());
				dataDictionaryValueDto.setCode(dataDictionaryValue.getName());
				dataDictionaryValueDto.setValue(dataDictionaryValue.getCode());
				if (dataDictionaryValue.getParentId() != null) {
					dataDictionaryValueDto.setParentId(dataDictionaryValue.getParentId());
				}
				if (dataDictionaryValue.getOrderNumber() != null) {
					dataDictionaryValueDto.setOrderNumber(dataDictionaryValue.getOrderNumber());
				}
				if (!WebUtil.strIsEmpty(dataDictionaryValue.getDescription())) {
					dataDictionaryValueDto.setNotes(dataDictionaryValue.getDescription());
				}
				if (dataDictionaryValue.getCreated() != null) {
					dataDictionaryValueDto.setCreated(dataDictionaryValue.getCreated());
				}
				if (dataDictionaryValue.getModified() != null) {
					dataDictionaryValueDto.setModified(dataDictionaryValue.getModified());
				}
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
		DataDictionary record = DTOUtils.newInstance(DataDictionary.class);
		record.setCode(t.getCode());
		int count = this.getActualDao().selectCount(record);
		if (count > 0) {
			throw new IllegalArgumentException(String.format("已存在编码为%s的记录", t.getCode()));
		}
		t.setYn(1);
		return super.insertSelective(t);
	}

	@Override
	public List<DataDictionary> listDataDictionary(DataDictionary ddit) {
		com.dili.uap.sdk.domain.DataDictionary uapDate = DTOUtils.newDTO(com.dili.uap.sdk.domain.DataDictionary.class);
		if (ddit.getId() != null) {
			uapDate.setId(ddit.getId());
		}
		if (!WebUtil.strIsEmpty(ddit.getCode())) {
			uapDate.setCode(ddit.getCode());
		}
		if (!WebUtil.strIsEmpty(ddit.getName())) {
			uapDate.setName(ddit.getName());
		}
		if (!WebUtil.strIsEmpty(ddit.getNotes())) {
			uapDate.setDescription(ddit.getNotes());
		}

		uapDate.setSystemCode(AlmConstants.ALM_SYSTEM_CODE);

		if (ddit.getCreated() != null) {
			uapDate.setCreated(ddit.getCreated());
		}
		if (ddit.getModified() != null) {
			uapDate.setModified(ddit.getModified());
		}

		List<com.dili.uap.sdk.domain.DataDictionary> data = this.dataDictionaryRpc.listDataDictionary(uapDate).getData();
		List<DataDictionary> dataDictionaryList = new ArrayList<DataDictionary>();
		for (com.dili.uap.sdk.domain.DataDictionary dataDictionary : data) {
			DataDictionary newDataDictionary = DTOUtils.newDTO(DataDictionary.class);
			newDataDictionary.setId(dataDictionary.getId());
			newDataDictionary.setCode(dataDictionary.getCode());
			newDataDictionary.setName(dataDictionary.getName());
			if (!WebUtil.strIsEmpty(dataDictionary.getDescription())) {
				newDataDictionary.setNotes(dataDictionary.getDescription());
			}
			if (dataDictionary.getCreated() != null) {
				newDataDictionary.setCreated(dataDictionary.getCreated());
			}
			if (dataDictionary.getModified() != null) {
				newDataDictionary.setModified(dataDictionary.getModified());
			}
			dataDictionaryList.add(newDataDictionary);
		}
		return dataDictionaryList;
	}

	@Override
	public void updateUapDataDictionaryList() {
		List<DataDictionary> selectAll = this.getActualDao().selectAll();
		for (DataDictionary dataDictionary : selectAll) {
			UapDataDictionaryDto uapDataDictionaryDto = DTOUtils.newDTO(UapDataDictionaryDto.class);
			uapDataDictionaryDto.setCode(dataDictionary.getCode());
			uapDataDictionaryDto.setName(dataDictionary.getName());
			uapDataDictionaryDto.setSystemCode(AlmConstants.ALM_SYSTEM_CODE);
			if (!WebUtil.strIsEmpty(dataDictionary.getNotes())) {
				uapDataDictionaryDto.setDescription(dataDictionary.getNotes());
			}
			if (dataDictionary.getCreated() != null) {
				uapDataDictionaryDto.setCreated(dataDictionary.getCreated());
			}
			if (dataDictionary.getModified() != null) {
				uapDataDictionaryDto.setModified(dataDictionary.getModified());
			}
			DataDictionaryValue queryValue = DTOUtils.newDTO(DataDictionaryValue.class);
			queryValue.setDdId(dataDictionary.getId());
			List<DataDictionaryValue> list = this.valueMapper.select(queryValue);
			List<com.dili.uap.sdk.domain.DataDictionaryValue> uapValues = new ArrayList<com.dili.uap.sdk.domain.DataDictionaryValue>();
			for (DataDictionaryValue dataDictionaryValue : list) {
				com.dili.uap.sdk.domain.DataDictionaryValue uapDataValue = DTOUtils.newDTO(com.dili.uap.sdk.domain.DataDictionaryValue.class);
				uapDataValue.setId(dataDictionaryValue.getId());
				uapDataValue.setCode(dataDictionaryValue.getCode());
				uapDataValue.setDdCode(dataDictionary.getCode());
				uapDataValue.setCode(dataDictionaryValue.getValue());
				uapDataValue.setName(dataDictionaryValue.getCode());

				if (dataDictionaryValue.getParentId() != null) {
					uapDataValue.setParentId(dataDictionaryValue.getParentId());
				}
				if (dataDictionaryValue.getOrderNumber() != null) {
					uapDataValue.setOrderNumber(dataDictionaryValue.getOrderNumber());
				}
				if (!WebUtil.strIsEmpty(dataDictionaryValue.getNotes())) {
					uapDataValue.setDescription(dataDictionaryValue.getNotes());
				}
				if (dataDictionaryValue.getCreated() != null) {
					uapDataValue.setCreated(dataDictionaryValue.getCreated());
				}
				if (dataDictionaryValue.getModified() != null) {
					uapDataValue.setModified(dataDictionaryValue.getModified());
				}
				uapValues.add(uapDataValue);
			}
			uapDataDictionaryDto.setDataDictionaryValues(uapValues);
			BaseOutput<Object> insertDataDictionaryDto = this.dataDictionaryRpc.insertDataDictionaryDto(uapDataDictionaryDto);
			if (insertDataDictionaryDto.getCode() != ResultCode.OK) {
				throw new RuntimeException("添加失败，code:" + uapDataDictionaryDto.getCode() + ",name:" + uapDataDictionaryDto.getName() + ",不能找到唯一的数据权限");
			}
		}

	}

}