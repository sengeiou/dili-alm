package com.dili.alm.component;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.alm.dao.SequenceMapper;
import com.dili.alm.dao.SequenceMapper.SequenceUpdateDto;
import com.dili.alm.domain.Sequence;
import com.dili.ss.dto.DTOUtils;

@Component
public class WorkOrderNumberGenerator implements NumberGenerator {

	private static final String WORK_ORDER_NUMBER_GENERATOR_TYPE = "workOrderNumberGenerator";

	@Autowired
	private SequenceMapper sequenceMapper;
	private AtomicInteger number = new AtomicInteger(0);

	@PostConstruct
	private void init() {
		Integer serialNumber = sequenceMapper.getByType(WORK_ORDER_NUMBER_GENERATOR_TYPE);
		if (serialNumber == null) {
			Sequence sequence = DTOUtils.newDTO(Sequence.class);
			sequence.setType(WORK_ORDER_NUMBER_GENERATOR_TYPE);
			sequence.setNumber(0);
			this.sequenceMapper.insertSelective(sequence);
			serialNumber = 0;
		}
		number.set(serialNumber);
	}

	@PreDestroy
	private void destory() {
		sequenceMapper.updateByType(new SequenceUpdateDto(this.number.get(), WORK_ORDER_NUMBER_GENERATOR_TYPE));
	}

	@Override
	public String get() {
		DecimalFormat df = new DecimalFormat("0000");
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return dateFormat.format(new Date()) + df.format(number.getAndIncrement());
	}

}
