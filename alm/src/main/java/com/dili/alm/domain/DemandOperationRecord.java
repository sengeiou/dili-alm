package com.dili.alm.domain;

import java.util.Date;
import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2018-04-09 17:19:52.
 */
@Table(name = "`hardware_apply_operation_record`")
public class DemandOperationRecord {
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;


    @Column(name = "`operation_name`")
    String operationName;


    @Column(name = "`description`")
    String description;

    @Column(name = "`operate_time`")
    Date operateTime;


    @Column(name = "`opertate_result`")
    Integer opertateResult;


    @Column(name = "`apply_id`")
    Long applyId;


    @Column(name = "`operation_type`")
    Integer operationType;


    @Column(name = "`operator_id`")
    Long operatorId;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getOperationName() {
		return operationName;
	}


	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Date getOperateTime() {
		return operateTime;
	}


	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}


	public Integer getOpertateResult() {
		return opertateResult;
	}


	public void setOpertateResult(Integer opertateResult) {
		this.opertateResult = opertateResult;
	}


	public Long getApplyId() {
		return applyId;
	}


	public void setApplyId(Long applyId) {
		this.applyId = applyId;
	}


	public Integer getOperationType() {
		return operationType;
	}


	public void setOperationType(Integer operationType) {
		this.operationType = operationType;
	}


	public Long getOperatorId() {
		return operatorId;
	}


	public void setOperatorId(Long operatorId) {
		this.operatorId = operatorId;
	}
    
    
    

}