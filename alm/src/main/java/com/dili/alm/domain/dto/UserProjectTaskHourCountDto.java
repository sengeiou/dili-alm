package com.dili.alm.domain.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class UserProjectTaskHourCountDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1431514780453732435L;

	// 用户id
	private Long userId;
	// 用户姓名
	private String realName;
	// 项目工时统计模型
	private Set<ProjectTaskHourCountDto> projectTaskHours = new HashSet<>();
	// 正常工时总计
	private Long totalTaskHour;
	// 加班工时总计
	private Long totalOverHour;
	// 所有工时总计
	private Long totalHour;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public Set<ProjectTaskHourCountDto> getProjectTaskHours() {
		return projectTaskHours;
	}

	public void setProjectTaskHours(Set<ProjectTaskHourCountDto> projectTaskHours) {
		this.projectTaskHours = projectTaskHours;
	}

	public Long getTotalTaskHour() {
		return totalTaskHour;
	}

	public void setTotalTaskHour(Long totalTaskHour) {
		this.totalTaskHour = totalTaskHour;
	}

	public Long getTotalOverHour() {
		return totalOverHour;
	}

	public void setTotalOverHour(Long totalOverHour) {
		this.totalOverHour = totalOverHour;
	}

	public Long getTotalHour() {
		return totalHour;
	}

	public void setTotalHour(Long totalHour) {
		this.totalHour = totalHour;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		UserProjectTaskHourCountDto other = (UserProjectTaskHourCountDto) obj;
		if (userId == null) {
			if (other.userId != null) {
				return false;
			}
		} else if (!userId.equals(other.userId)) {
			return false;
		}
		return true;
	}
}
