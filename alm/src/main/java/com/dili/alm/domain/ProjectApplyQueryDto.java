package com.dili.alm.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.dili.ss.domain.annotation.Operator;

/**
 * 由MyBatis Generator工具自动生成
 *
 * This file was generated on 2017-12-18 14:59:21.
 */
@Table(name = "`project_apply`")
public interface ProjectApplyQueryDto extends ProjectApply {

	@Operator(Operator.GREAT_EQUAL_THAN)
	@Column(name = "`created`")
	Date getCreatedStart();

	void setCreatedStart(Date created);

}