package com.dili.alm.component;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.alm.constant.AlmConstants;
import com.dili.alm.dao.SequenceMapper;
import com.dili.alm.domain.Sequence;
import com.dili.ss.dto.DTOUtils;

import tk.mybatis.mapper.entity.Example;

@Component
public class DemandNumberGenerator extends AbstractNumberGenerator {

	private static final String DEMAND_NUMBER_GENERATOR_TYPE = "demandNumberGenerator";

	@Autowired
	private SequenceMapper sequenceMapper;
	private DateFormat dfTime = new SimpleDateFormat("yyyyMMddHHmm");
	private AtomicInteger number = new AtomicInteger(0);
public static void main(String[] args) {
	 DateFormat dfTime1 = new SimpleDateFormat("yyyyMMddHHmm");
	System.out.println();
}
	@Override
	public void init() {
		Sequence sequenceQuery = DTOUtils.newDTO(Sequence.class);
		sequenceQuery.setType(DEMAND_NUMBER_GENERATOR_TYPE);
		Sequence sequence = sequenceMapper.selectOne(sequenceQuery);
		if (sequence == null){
			sequenceQuery.setNumber(1);
			this.sequenceMapper.insertSelective(sequenceQuery);
			number.set(1);
			return;
		}
		number.set(sequence.getNumber());
		sequence.setNumber(sequence.getNumber() + AlmConstants.SEQUENCE_NUMBER_STEP_LENGTH);
		this.sequenceMapper.updateByPrimaryKey(sequence);
	}

	@Override
	public String get() {
		DecimalFormat df = new DecimalFormat("000");
		Integer sequence = number.getAndIncrement();
		if (sequence % AlmConstants.DEMAND_SEQUENCE_NUMBER_STEP_LENGTH == 0) {
			this.persist();
		}
		return this.dfTime.format(new Date())+df.format(sequence);
	}

	@Override
	public void persist() {
		Sequence record = DTOUtils.newDTO(Sequence.class);
		record.setNumber(this.number.get());
		Example example = new Example(Sequence.class);
		example.createCriteria().andEqualTo("type", DEMAND_NUMBER_GENERATOR_TYPE);
		sequenceMapper.updateByExampleSelective(record, example);
	}

}
