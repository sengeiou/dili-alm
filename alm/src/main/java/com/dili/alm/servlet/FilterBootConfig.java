package com.dili.alm.servlet;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dili.uap.sdk.session.SessionFilter;

/**
 * Created by asiamaster on 2017/7/5 0005.
 */
@Configuration
public class FilterBootConfig {

	@Bean
	public FilterRegistrationBean filterRegistrationBean(SessionFilter sessionFilter){
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(sessionFilter);
		filterRegistrationBean.setEnabled(true);
		filterRegistrationBean.addUrlPatterns("/*");
		return filterRegistrationBean;
	}
}
