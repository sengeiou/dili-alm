package com.dili.alm.service.impl;

import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.DataDictionaryMapper;
import com.dili.alm.dao.DataDictionaryValueMapper;
import com.dili.alm.domain.DataDictionary;
import com.dili.alm.domain.DataDictionaryValue;
import com.dili.alm.domain.dto.DataDictionaryDto;
import com.dili.alm.domain.dto.DataDictionaryValueDto;
import com.dili.alm.domain.dto.DataDictionaryValueTreeView;
import com.dili.alm.domain.dto.UapDataDictionaryDto;
import com.dili.alm.rpc.DataDictionaryRpc;
import com.dili.alm.service.DataDictionaryValueService;
import com.dili.alm.utils.WebUtil;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ��MyBatis Generator�����Զ����� This file was generated on 2017-07-12
 * 10:41:19.
 */
@Service
public class DataDictionaryValueServiceImpl extends BaseServiceImpl<DataDictionaryValue, Long> implements DataDictionaryValueService {

	@Autowired
	private DataDictionaryMapper dataDictionaryMapper;
	@Autowired
	private DataDictionaryRpc dataDictionaryRpc;
	
	public DataDictionaryValueMapper getActualDao() {
		return (DataDictionaryValueMapper) getDao();
	}

	@Override
	public BaseOutput<Object> insertAndGet(DataDictionaryValue dataDictionaryValue) {
		int result = this.insert(dataDictionaryValue);
		if (result <= 0) {
			return BaseOutput.failure();
		}
		dataDictionaryValue = this.getActualDao().selectByPrimaryKey(dataDictionaryValue.getId());
		return BaseOutput.success().setData(dataDictionaryValue);
	}

	@Override
	public List<DataDictionaryValueTreeView> listTree(Long ddId) {
		DataDictionary dd = this.dataDictionaryMapper.selectByPrimaryKey(ddId);
		if (dd == null) {
			return null;
		}
		DataDictionaryValueTreeView root = new DataDictionaryValueTreeView();
		root.setId(-1L);
		root.addAttribute("ddId", dd.getId());
		root.setName(dd.getName());
		List<DataDictionaryValueTreeView> target = new ArrayList<>();
		target.add(root);
		DataDictionaryValue record = DTOUtils.newDTO(DataDictionaryValue.class);
		record.setDdId(ddId);
		List<DataDictionaryValue> list = this.getActualDao().select(record);
		if (CollectionUtils.isEmpty(list)) {
			return target;
		}
		for (DataDictionaryValue ddValue : list) {
			DataDictionaryValueTreeView vo = new DataDictionaryValueTreeView();
			vo.setId(ddValue.getId());
			vo.addAttribute("ddId", ddValue.getDdId());
			vo.setName(ddValue.getCode());
			if (ddValue.getParentId() == null) {
				vo.setParentId(-1L);
			} else {
				vo.setParentId(ddValue.getParentId());
			}
			target.add(vo);
		}
		return target;
	}

	@Override
	public int insert(DataDictionaryValue t) {
		t.setYn(1);
		return super.insert(t);
	}

	@Override
	public int insertSelective(DataDictionaryValue t) {
		t.setYn(1);
		return super.insertSelective(t);
	}

	@Override
	public List<DataDictionaryValueDto> listDataDictionaryValueByCode(String ddCode) {
		UapDataDictionaryDto model = dataDictionaryRpc.findByCode(ddCode, AlmConstants.ALM_SYSTEM_CODE).getData();
		if (model == null) {
			return null;
		}
		List<DataDictionaryValueDto> values =new ArrayList<DataDictionaryValueDto>();
		List<com.dili.uap.sdk.domain.DataDictionaryValue> DataDictionaryValues = model.getDataDictionaryValues();
		if (CollectionUtils.isNotEmpty(values)) {
			for (com.dili.uap.sdk.domain.DataDictionaryValue dataDictionaryValue : DataDictionaryValues) {
				DataDictionaryValueDto dataDictionaryValueDto=DTOUtils.newDTO(DataDictionaryValueDto.class);
				dataDictionaryValueDto.setId(dataDictionaryValue.getId());
				dataDictionaryValueDto.setDdId(model.getId());
				dataDictionaryValueDto.setParentId(dataDictionaryValue.getParentId());
				dataDictionaryValueDto.setOrderNumber(dataDictionaryValue.getOrderNumber());
				dataDictionaryValueDto.setCode(dataDictionaryValue.getName());
				dataDictionaryValueDto.setValue(dataDictionaryValue.getCode());
				dataDictionaryValueDto.setNotes(dataDictionaryValue.getDescription());
				dataDictionaryValueDto.setCreated(dataDictionaryValue.getCreated());
				dataDictionaryValueDto.setModified(dataDictionaryValue.getModified());
				values.add(dataDictionaryValueDto);
			}
		}
		return values;
	}

	@Override
	public List<DataDictionaryValueDto> listDataDictionaryValue(DataDictionaryValue ddv) {
		com.dili.uap.sdk.domain.DataDictionaryValue  uapValueDto=DTOUtils.newDTO(com.dili.uap.sdk.domain.DataDictionaryValue.class);
		if(ddv.getId()!=null) {
			uapValueDto.setId(ddv.getId());
		}
		if(ddv.getDdId()!=null) {
			com.dili.uap.sdk.domain.DataDictionary uapData=DTOUtils.newDTO(com.dili.uap.sdk.domain.DataDictionary.class);
			uapData.setId(ddv.getDdId());
			List<com.dili.uap.sdk.domain.DataDictionary> listDataDictionary = this.dataDictionaryRpc.listDataDictionary(uapData).getData();
			if(listDataDictionary!=null&&listDataDictionary.size()>0) {
				uapValueDto.setDdCode(listDataDictionary.get(0).getCode());
			}
			
		}
		if(ddv.getParentId()!=null) {
			uapValueDto.setParentId(ddv.getParentId());
		}
		if(ddv.getOrderNumber()!=null) {
			uapValueDto.setOrderNumber(ddv.getOrderNumber());
		}
		if(!WebUtil.strIsEmpty(ddv.getCode())) {
			uapValueDto.setName(ddv.getCode());	
		}
		if(!WebUtil.strIsEmpty(ddv.getValue())) {
			uapValueDto.setCode(ddv.getValue());
		}
		if(!WebUtil.strIsEmpty(ddv.getNotes())) {
			uapValueDto.setDescription(ddv.getNotes());
		}
		if(ddv.getCreated()!=null) {
			uapValueDto.setCreated(ddv.getCreated());
		}
		if(ddv.getModified()!=null) {
			uapValueDto.setModified(ddv.getModified());
		}
		List<com.dili.uap.sdk.domain.DataDictionaryValue> DataDictionaryValues = dataDictionaryRpc.listDataDictionaryValue(uapValueDto).getData();
		List<DataDictionaryValueDto> values =new ArrayList<DataDictionaryValueDto>();
		if (CollectionUtils.isNotEmpty(DataDictionaryValues)) {
			for (com.dili.uap.sdk.domain.DataDictionaryValue dataDictionaryValue : DataDictionaryValues) {
				DataDictionaryValueDto dataDictionaryValueDto=DTOUtils.newDTO(DataDictionaryValueDto.class);
				dataDictionaryValueDto.setId(dataDictionaryValue.getId());
				dataDictionaryValueDto.setCode(dataDictionaryValue.getCode());
				UapDataDictionaryDto model = dataDictionaryRpc.findByCode(dataDictionaryValue.getDdCode(), AlmConstants.ALM_SYSTEM_CODE).getData();
				dataDictionaryValueDto.setDdId(model.getId());
				dataDictionaryValueDto.setParentId(dataDictionaryValue.getParentId());
				dataDictionaryValueDto.setOrderNumber(dataDictionaryValue.getOrderNumber());
				dataDictionaryValueDto.setCode(dataDictionaryValue.getName());
				dataDictionaryValueDto.setValue(dataDictionaryValue.getCode());
				dataDictionaryValueDto.setNotes(dataDictionaryValue.getDescription());
				dataDictionaryValueDto.setCreated(dataDictionaryValue.getCreated());
				dataDictionaryValueDto.setModified(dataDictionaryValue.getModified());
				values.add(dataDictionaryValueDto);
			}
		}
		return values;
	}


}