package com.dili.alm.component;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.alm.dao.SequenceMapper;
import com.dili.alm.domain.Sequence;
import com.dili.ss.dto.DTOUtils;

import tk.mybatis.mapper.entity.Example;

@Component
public class HardwareApplyNumberGenerator implements NumberGenerator {

	private static final String HARDWARE_APPLY_NUMBER_GENERATOR_TYPE = "hardwareApplyNumberGenerator";

	@Autowired
	private SequenceMapper sequenceMapper;
	private AtomicInteger number = new AtomicInteger(0);

	@Override
	public void init() {
		Sequence sequenceQuery = DTOUtils.newDTO(Sequence.class);
		sequenceQuery.setType(HARDWARE_APPLY_NUMBER_GENERATOR_TYPE);
		Sequence sequence = sequenceMapper.selectOne(sequenceQuery);
		if (sequence == null) {
			this.sequenceMapper.insertSelective(sequenceQuery);
			number.set(1);
			return;
		}
		number.set(sequence.getNumber());
	}

	@Override
	public String get() {
		DecimalFormat df = new DecimalFormat("00000");
		return df.format(number.getAndIncrement());
	}

	@Override
	public void persist() {
		Sequence record = DTOUtils.newDTO(Sequence.class);
		record.setNumber(this.number.get());
		Example example = new Example(Sequence.class);
		example.createCriteria().andEqualTo("type", HARDWARE_APPLY_NUMBER_GENERATOR_TYPE);
		sequenceMapper.updateByExampleSelective(record, example);
	}

}
