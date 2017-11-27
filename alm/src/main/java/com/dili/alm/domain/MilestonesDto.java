package com.dili.alm.domain;

import java.util.List;

import javax.persistence.Column;

import com.dili.ss.domain.annotation.Operator;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2017-10-20 11:02:17.
 */
public interface MilestonesDto extends ProjectVersion {
    @Column(name = "`project_id`")
    @Operator(Operator.IN)
    List<Long> getProjectIds();
    void setProjectIds(List<Long> projectIds);
}