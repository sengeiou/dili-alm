package com.dili;

import java.io.IOException;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.dili.alm.cache.AlmCache;
import com.dili.ss.retrofitful.annotation.RestfulScan;

import tk.mybatis.spring.annotation.MapperScan;

/**
 * 由MyBatis Generator工具自动生成
 */
@ServletComponentScan
@SpringBootApplication
@EnableTransactionManagement
@MapperScan(basePackages = { "com.dili.alm.dao", "com.dili.ss.dao", "com.dili.ss.quartz.dao" })
@ComponentScan(basePackages = { "com.dili.ss", "com.dili.alm", "com.dili.sysadmin" })
@RestfulScan("com.dili.alm.rpc")
@EnableAsync
/**
 * 除了内嵌容器的部署模式，Spring Boot也支持将应用部署至已有的Tomcat容器, 或JBoss, WebLogic等传统Java EE应用服务器。
 * 以Maven为例，首先需要将<packaging>从jar改成war，然后取消spring-boot-maven-plugin，然后修改Application.java
 * 继承SpringBootServletInitializer
 */
public class AlmApplication extends SpringBootServletInitializer implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("http://almadmin.diligrp.com").allowCredentials(true)
				.allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept");
	}

	@Bean
	public GroupTemplate mailContentTemplate() throws IOException {
		Configuration cfg = Configuration.defaultConfiguration();
		StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
		return new GroupTemplate(resourceLoader, cfg);
	}

	@Bean
	public AlmCache almCache() {
		return AlmCache.getInstance();
	}

	public static void main(String[] args) {
		SpringApplication.run(AlmApplication.class, args);
	}

}
