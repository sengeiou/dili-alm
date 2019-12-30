package com.dili.sysadmin.domain.dto;

import com.dili.sysadmin.domain.UserDataAuth;



public class UserDataAuthDto extends UserDataAuth {
		private Long id;
		
	    private Long userId;
		
    	private Long DataId;

    	private String type;
    	
		public Long getDataId() {
			return DataId;
		}

		public void setDataId(Long dataId) {
			DataId = dataId;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getUserId() {
			return userId;
		}

		public void setUserId(Long userId) {
			this.userId = userId;
		}
    	
    	
}