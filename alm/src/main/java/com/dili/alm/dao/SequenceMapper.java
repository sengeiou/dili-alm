package com.dili.alm.dao;

import com.dili.alm.domain.Sequence;
import com.dili.ss.base.MyMapper;

public interface SequenceMapper extends MyMapper<Sequence> {

    Integer get();

    void update(int number);

}