package com.dili;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.MultipartConfigElement;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.manager.WorkOrderManager;
import com.dili.ss.dto.DTOScan;
import com.dili.ss.retrofitful.annotation.RestfulScan;

import tk.mybatis.spring.annotation.MapperScan;

/**
 * 由MyBatis Generator工具自动生成
 */
@ServletComponentScan
@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.dili.ss", "com.dili.alm", "com.dili.uap.sdk" })
@DTOScan(value = { "com.dili.ss", "com.dili.uap.sdk", "com.dili.alm" })
@RestfulScan({ "com.dili.alm.rpc", "com.dili.uap.sdk.rpc", "com.dili.bpmc.sdk.rpc" })
@MapperScan(basePackages = { "com.dili.alm.dao", "com.dili.ss.dao", "com.dili.ss.quartz.dao" })
@EnableAsync
/**
 * 除了内嵌容器的部署模式，Spring Boot也支持将应用部署至已有的Tomcat容器, 或JBoss, WebLogic等传统Java EE应用服务器。
 * 以Maven为例，首先需要将<packaging>从jar改成war，然后取消spring-boot-maven-plugin，然后修改Application.java
 * 继承SpringBootServletInitializer
 */
public class AlmApplication extends SpringBootServletInitializer {

	@Bean
	public FilterRegistrationBean corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin(CorsConfiguration.ALL);
		config.addAllowedHeader(CorsConfiguration.ALL);
		config.addAllowedMethod(CorsConfiguration.ALL);
		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		return bean;
	}

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		String tempUrl = System.getProperty("java.io.tmpdir");
		System.out.println("临时目录：" + tempUrl);
		factory.setLocation(tempUrl);
		return factory.createMultipartConfig();
	}

	@Bean
	public GroupTemplate mailContentTemplate() throws IOException {
		Configuration cfg = Configuration.defaultConfiguration();
		StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
		return new GroupTemplate(resourceLoader, cfg);
	}

	@Bean
	public Map<Integer, WorkOrderManager> workOrderManagerMap(List<WorkOrderManager> workOrderManagers) {
		Map<Integer, WorkOrderManager> map = new HashMap<>(workOrderManagers.size());
		workOrderManagers.forEach(m -> map.put(m.getType().getValue(), m));
		return map;
	}

	@Bean
	public AlmCache almCache() {
		return AlmCache.getInstance();
	}

	public static void main(String[] args) {
		SpringApplication.run(AlmApplication.class, args);
	}

}
