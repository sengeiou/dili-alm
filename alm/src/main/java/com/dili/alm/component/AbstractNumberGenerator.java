package com.dili.alm.component;

import org.springframework.beans.factory.InitializingBean;

public abstract class AbstractNumberGenerator implements NumberGenerator, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		this.init();
	}
}
