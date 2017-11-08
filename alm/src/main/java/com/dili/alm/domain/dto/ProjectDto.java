package com.dili.alm.domain.dto;

import com.dili.alm.domain.Project;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2017-10-18 17:22:54.
 */
@Table(name = "`project`")
public interface ProjectDto extends Project {
    @Column(name = "`id`")
//    @Operator(Operator.IN)
    List<Long> getIds();
    void setIds(List<Long> ids);
}