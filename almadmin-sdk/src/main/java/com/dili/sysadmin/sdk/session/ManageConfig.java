package com.dili.sysadmin.sdk.session;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.dili.sysadmin.sdk.util.ManageRedisUtil;
import com.dili.sysadmin.sdk.util.WebContent;

/**
 * 权限配置信息<BR>
 * 优先读取conf/manage-${spring.profiles.active}.properties<BR>
 * 找不到就读取conf/manage.properties<BR>
 * Created by asiamaster on 2017/7/4 0004.
 */
@Component
@ConfigurationProperties(prefix = "manage")
@PropertySource({ "classpath:conf/manage-${spring.profiles.active}.properties" })
public class ManageConfig {

	// @Value("${manage.domain}")
	private String domain;
	// 是否开启过滤
	// @Value("${manage.enable}")
	private Boolean enable = true;

	// 包含列表
	// @Value("#{'${manage.includes}'.split(',')}")
	private List<String> includes;

	// 排除列表
	// @Value("#{'${manage.excludes}'.split(',')}")
	private List<String> excludes;
	// 是否必须在框架内
	// @Value("${manage.mustIframe}")
	private Boolean mustIframe = true;

	private Boolean userLimitOne = false;

	@Autowired
	private ManageRedisUtil redisUtil;

	/**
	 * 是否包含
	 * 
	 * @return
	 */
	private boolean isInclude() {
		if (WebContent.getRequest() == null) {
			return true;
		}
		return urlFilter(includes);
	}

	/**
	 * 是否排除
	 * 
	 * @return
	 */
	private boolean isExclude() {
		if (WebContent.getRequest() == null) {
			return false;
		}
		return urlFilter(excludes);
	}

	private Boolean urlFilter(List<String> patternStrs) {
		String uri = WebContent.getRequest().getRequestURI();
		for (String str : patternStrs) {
			if (Pattern.compile(str).matcher(uri).find()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 验证权限是否通过
	 * 
	 * @return
	 */
	public boolean hasChecked() {
		if (!enable) {
			return false;
		}
		if (isExclude()) {
			return false;
		}
		if (isInclude()) {
			return true;
		}
		return false;
	}

	public String getDomain() {
		return domain;
	}

	public ManageRedisUtil getRedisUtil() {
		return redisUtil;
	}

	public Boolean getEnable() {
		return enable;
	}

	public List<String> getIncludes() {
		return includes;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public Boolean getMustIframe() {
		return mustIframe;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public void setMustIframe(Boolean mustIframe) {
		this.mustIframe = mustIframe;
	}

	public Boolean getUserLimitOne() {
		return userLimitOne;
	}

	public void setUserLimitOne(Boolean userLimitOne) {
		this.userLimitOne = userLimitOne;
	}
}