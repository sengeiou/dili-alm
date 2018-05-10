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

@Component
public class ProjectOnlineApplyNumberGenerator implements NumberGenerator {

	public static final String PROJECT_ONLINE_APPLY_NUMBER_GENERATOR_TYPE = "projectOnlineApplyNumberGenerator";

	@Autowired
	private SequenceMapper sequenceMapper;
	private DateFormat df = new SimpleDateFormat("yyMMdd");

	private AtomicInteger number = new AtomicInteger(0);

	@PostConstruct
	private void init() {
		number.set(sequenceMapper.getByType(PROJECT_ONLINE_APPLY_NUMBER_GENERATOR_TYPE));
	}

	@PreDestroy
	private void destory() {
		sequenceMapper
				.updateByType(new SequenceUpdateDto(this.number.get(), PROJECT_ONLINE_APPLY_NUMBER_GENERATOR_TYPE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dili.alm.cache.NumberGenerator#get()
	 */
	@Override
	public String get() {
		DecimalFormat df = new DecimalFormat("0000");
		return this.df.format(new Date()) + df.format(number.getAndIncrement());
	}

}
