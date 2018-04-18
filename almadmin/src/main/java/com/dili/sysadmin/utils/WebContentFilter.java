package com.dili.sysadmin.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.dili.sysadmin.sdk.util.WebContent;

/**
 * User: juqkai (juqkai@gmail.com) Date: 13-9-25 上午11:23
 */
public class WebContentFilter implements Filter {
	private Pattern[] exclude;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String excludeString = filterConfig.getInitParameter("exclude");
		if (!StringUtils.isBlank(excludeString)) {
			String[] excludeArray = excludeString.split(",");
			exclude = new Pattern[excludeArray.length];
			for (int i = 0; i < excludeArray.length; i++) {
				exclude[i] = Pattern.compile(excludeArray[i]);
			}
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (isExclude((HttpServletRequest) request)) {
			chain.doFilter(request, response);
			return;
		}
		try {
			WebContent.put((HttpServletRequest) request);
			WebContent.put((HttpServletResponse) response);
			initParam((HttpServletRequest) request);
			chain.doFilter(request, response);
		} finally {
			WebContent.clean();
		}
	}

	private void initParam(HttpServletRequest request) {
		// COOKIE
		Map<String, Object> cookie = new HashMap<String, Object>();
		if (null != request.getCookies()) {
			for (Cookie c : request.getCookies()) {
				cookie.put(c.getName(), c.getValue());
			}
		}
		request.setAttribute("c", cookie);
		request.setAttribute("cookie", cookie);
	}

	@Override
	public void destroy() {
		WebContent.clean();
	}

	private boolean isExclude(HttpServletRequest request) {
		if (exclude == null || exclude.length == 0) {
			return false;
		}
		String currentUri = fetchPath(request);
		for (Pattern excludeUri : exclude) {
			if (excludeUri.matcher(currentUri).find()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取系统内的请求路径
	 * 
	 * @param request
	 * @return
	 */
	private String fetchPath(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		String currentUri = request.getRequestURI();
		currentUri = currentUri.replaceAll("^" + contextPath, "");
		return currentUri;
	}

}
