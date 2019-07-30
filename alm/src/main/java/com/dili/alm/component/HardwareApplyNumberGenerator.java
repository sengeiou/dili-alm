package com.dili.alm.component;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.SequenceMapper;
import com.dili.alm.domain.Sequence;
import com.dili.ss.dto.DTOUtils;

import tk.mybatis.mapper.entity.Example;

@Component
public class HardwareApplyNumberGenerator extends AbstractNumberGenerator {

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
			sequenceQuery.setNumber(AlmConstants.SEQUENCE_NUMBER_STEP_LENGTH + 1);
			this.sequenceMapper.insertSelective(sequenceQuery);
			number.set(AlmConstants.SEQUENCE_NUMBER_STEP_LENGTH + 1);
			return;
		}
		number.set(sequence.getNumber());
		sequence.setNumber(sequence.getNumber() + AlmConstants.SEQUENCE_NUMBER_STEP_LENGTH);
		this.sequenceMapper.updateByPrimaryKey(sequence);
	}

	@Override
	public String get() {
		DecimalFormat df = new DecimalFormat("00000");
		Integer sequence = number.getAndIncrement();
		if (sequence % AlmConstants.SEQUENCE_NUMBER_STEP_LENGTH == 0) {
			this.persist();
		}
		return df.format(sequence);
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
