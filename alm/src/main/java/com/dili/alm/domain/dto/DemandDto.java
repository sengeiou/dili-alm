package com.dili.alm.domain.dto;

import com.dili.alm.domain.Demand;
import com.dili.ss.domain.BaseDomain;
import com.dili.ss.domain.annotation.Operator;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import java.util.List;

import javax.persistence.*;


public class DemandDto extends Demand {
	@Operator(Operator.IN)
	@Column(name = "`id`")
	List<Long> ids;

	public List<Long> getIds() {
		return ids;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
	
	
}