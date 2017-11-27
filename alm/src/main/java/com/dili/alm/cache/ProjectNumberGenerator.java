package com.dili.alm.cache;

import com.dili.alm.dao.SequenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ProjectNumberGenerator {
    @Autowired
    private SequenceMapper sequenceMapper;

    private AtomicInteger number = new AtomicInteger(0);

    @PostConstruct
    private void init() {
        number.set(sequenceMapper.get());
    }

    @PreDestroy
    private void destory() {
        sequenceMapper.update(number.get());
    }

    /**
     * 格式化序列
     * @return 序列 xxxx
     */
    public String get() {
        DecimalFormat df = new DecimalFormat("0000");
        return df.format(number.getAndIncrement());
    }
}
