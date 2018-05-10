package com.dili.alm.domain.dto;

import com.dili.alm.domain.HardwareResourceRequirement;
import com.dili.ss.domain.BaseDomain;

public class HardwareResourceRequirementDto extends BaseDomain implements HardwareResourceRequirement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3129604121287923856L;

	// CPU数量
	private Integer cpuAmount;
	// 内存
	private Integer memoryAmount;
	// 磁盘
	private Integer diskAmount;
	// 备注
	private String notes;
	private Long applyId;

	@Override
	public Integer getCpuAmount() {
		return cpuAmount;
	}

	@Override
	public void setCpuAmount(Integer cpuAmount) {
	  this.cpuAmount = cpuAmount;
		
	}

	@Override
	public Integer getMemoryAmount() {
		return memoryAmount;
	}

	@Override
	public void setMemoryAmount(Integer memoryAmount) {
		 this.memoryAmount = memoryAmount;
	}

	@Override
	public Integer getDiskAmount() {
		return diskAmount;
	}

	@Override
	public void setDiskAmount(Integer diskAmount) {
		this.diskAmount = diskAmount;
		
	}

	@Override
	public String getNotes() {
		return notes;
	}

	@Override
	public void setNotes(String notes) {
		this.notes = notes;
		
	}

	@Override
	public Long getApplyId() {
		return applyId;
	}

	@Override
	public void setApplyId(Long applyId) {
		this.applyId = applyId;
		
	}

}
