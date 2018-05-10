package com.dili.alm.dao;

import com.dili.alm.domain.Sequence;
import com.dili.ss.base.MyMapper;

public interface SequenceMapper extends MyMapper<Sequence> {

	public static final class SequenceUpdateDto {
		private Integer number;
		private String type;

		public SequenceUpdateDto() {
			super();
			// TODO Auto-generated constructor stub
		}

		public SequenceUpdateDto(Integer number, String type) {
			super();
			this.number = number;
			this.type = type;
		}

		public Integer getNumber() {
			return number;
		}

		public void setNumber(Integer number) {
			this.number = number;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}

	Integer getByType(String type);

	void updateByType(SequenceUpdateDto param);

}