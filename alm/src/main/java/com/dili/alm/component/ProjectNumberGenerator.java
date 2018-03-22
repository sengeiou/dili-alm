package com.dili.alm.component;

import com.dili.alm.dao.SequenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ProjectNumberGenerator implements NumberGenerator {
    @Autowired
    private SequenceMapper sequenceMapper;

    private AtomicInteger number = new AtomicInteger(0);

    @PostConstruct
    private void init() {
        number.set(sequenceMapper.get());
    }

    @PreDestroy
    private void destory() {
        sequenceMapper.updateByType(null);
    }

    /* (non-Javadoc)
	 * @see com.dili.alm.cache.NumberGenerator#get()
	 */
    @Override
	public String get() {
        DecimalFormat df = new DecimalFormat("0000");
        return df.format(number.getAndIncrement());
    }
}
