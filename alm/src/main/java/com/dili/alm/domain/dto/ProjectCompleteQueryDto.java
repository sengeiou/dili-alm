package com.dili.alm.domain.dto;

import java.util.Date;

import javax.persistence.Column;

import com.dili.alm.domain.ProjectComplete;
import com.dili.ss.domain.annotation.Operator;

public interface ProjectCompleteQueryDto extends ProjectComplete {

	@Operator(Operator.GREAT_EQUAL_THAN)
    @Column(name = "`created`")
    Date getCreatedStart();
	
    void setCreatedStart(Date created);
	
}
