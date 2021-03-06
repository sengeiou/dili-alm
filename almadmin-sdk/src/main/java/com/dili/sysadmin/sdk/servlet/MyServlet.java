package com.dili.sysadmin.sdk.servlet;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

/**
 * 当应用只有默认的servlet（即DispatcherServlet）时，映射的url为/,存在其他的servlet时，映射的路径为servlet的注册的beanname(可通过@Component注解修改)，创建方式如下：
 * Created by asiam on 2017/4/20 0020.
 */
//@Component("myServlet")
public class MyServlet implements Servlet {
    /**
     * @see javax.servlet.Servlet#destroy()
     */
    @Override
    public void destroy() {
        System.out.println("destroy...");
    }

    /**
     * @return
     * @see javax.servlet.Servlet#getServletConfig()
     */
    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    /**
     * @return
     * @see javax.servlet.Servlet#getServletInfo()
     */
    @Override
    public String getServletInfo() {
        return null;
    }

    /**
     * @param arg0
     * @throws ServletException
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig arg0) throws ServletException {
        System.out.println("servlet init...");
    }

    /**
     * @param arg0
     * @param arg1
     * @throws ServletException
     * @throws IOException
     * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException,
            IOException {
        System.out.println("service...");
    }
}
