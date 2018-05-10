package com.dili.alm.component;

import com.dili.alm.dao.SequenceMapper;
import com.dili.alm.dao.SequenceMapper.SequenceUpdateDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ProjectNumberGenerator implements NumberGenerator {

	public static final String PROJECT_NUMBER_GENERATOR_TYPE = "projectNumberGenerator";

	@Autowired
	private SequenceMapper sequenceMapper;

	private AtomicInteger number = new AtomicInteger(0);

	@PostConstruct
	private void init() {
		number.set(sequenceMapper.getByType(PROJECT_NUMBER_GENERATOR_TYPE));
	}

	@PreDestroy
	private void destory() {
		sequenceMapper.updateByType(new SequenceUpdateDto(this.number.get(), PROJECT_NUMBER_GENERATOR_TYPE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dili.alm.cache.NumberGenerator#get()
	 */
	@Override
	public String get() {
		DecimalFormat df = new DecimalFormat("0000");
		return df.format(number.getAndIncrement());
	}
}
