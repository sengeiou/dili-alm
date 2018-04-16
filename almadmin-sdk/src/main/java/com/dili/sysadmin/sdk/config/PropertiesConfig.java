package com.dili.sysadmin.sdk.config;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.context.support.StandardServletEnvironment;

/**
 * Created by asiamaster on 2017/7/4 0004.
 */
//@Configuration
public class PropertiesConfig {

	@Autowired
	private StandardServletEnvironment env;

//	@PostConstruct
//	public void setup() throws IOException {
//		Resource fileSystemResource = new ClassPathResource("application.properties");
//		Properties result = new Properties();
//		PropertiesLoaderUtils.fillProperties(result, fileSystemResource);
//		String active = result.getProperty("spring.profiles.active");
//
//		try {
//			ClassPathResource resource = new ClassPathResource("manage-"+active+".properties");
//			if(!resource.exists()){
//				resource = new ClassPathResource("manage.properties");
//			}
//			if(resource.exists()) {
//				env.getPropertySources().addLast(new ResourcePropertySource("custom", resource));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
////		Resource resource = new ClassPathResource("application.properties");
////		Properties result = new Properties();
////		PropertiesLoaderUtils.fillProperties(result, resource);
//	}
	/**
	 * 指定读取manage-"+env+".properties，如果没有该文件则直接读取manage.properties
	 * @return
	 * @throws IOException
	 */
//	@Bean
	public PropertySourcesPlaceholderConfigurer createPropertySourcesPlaceholderConfigurer() throws IOException {
		final PropertySourcesPlaceholderConfigurer ppc = new PropertySourcesPlaceholderConfigurer();
		ppc.setIgnoreResourceNotFound(true);
		ppc.setEnvironment(env);
		ppc.setTrimValues(true);
		Resource applicationClassPathResource = new ClassPathResource("application.properties"); // your file
		Properties result = new Properties();
		PropertiesLoaderUtils.fillProperties(result, applicationClassPathResource);
		String active = result.getProperty("spring.profiles.active");
		try {
			ClassPathResource manageClassPathResource = new ClassPathResource("manage-"+active+".properties");
			if(!manageClassPathResource.exists()){
				manageClassPathResource = new ClassPathResource("manage.properties");
			}
			if(!manageClassPathResource.exists()){
				return ppc;
			}
			ppc.setLocation(manageClassPathResource);
			return ppc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
