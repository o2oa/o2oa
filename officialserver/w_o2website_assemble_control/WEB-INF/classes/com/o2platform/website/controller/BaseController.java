package com.o2platform.website.controller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * BaseController为所有的Controller的父类，系统在BaseController类里提供了部分方法来供子类使用：<br/>
 * getWebApplicationContext 获取Spring的上下文对象<br/>
 * getServletContext 获取Servlet上下文对象，一般用来缓存常用的对象（application对象）<br/>
 * getAttributeFromServletContext 从Servlet上下文中获取指定的属性，一般是获取缓存对象<br/>
 * setAttributeToServletContext 向Servlet上下文对象中设置属性，一般是设置缓存对象<br/>
 * getSession 获取Session对象<br/>
 * getAttributeFromHttpSession 从Session中获取指定的属性，一般是获取用户信息，购物车信息<br/>
 * setAttributeToHttpSession  向session中设置属性，一般是设置用户相关属性<br/>
 * getRequest  获取HttpRequest对象，一般是获取请求的参数<br/>
 */
@Controller
public class BaseController {
	
	/**
	 * 在Controller中获取ServletContext对象
	 * @return
	 */
	public Object getServiceBean(String benaName) {
		
		try {
			WebApplicationContext springContext = getWebApplicationContext();
			return springContext.getBean(benaName);
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 在Controller中获取ServletContext对象
	 * @return
	 */
	public WebApplicationContext getWebApplicationContext() {
		WebApplicationContext springContext = null;
		try {
			springContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		} catch (Exception e) {
		}
		return springContext;
	}
	
	/**
	 * 在Controller中获取ServletContext对象
	 * @return
	 */
	public ServletContext getServletContext() {
		ServletContext servletContext = null;
		try {
			servletContext = getSession().getServletContext();
		} catch (Exception e) {
		}
		return servletContext;
	}
	
	/**
	 * 从ServletContext中获取参数
	 * @return
	 */
	public Object getAttributeFromServletContext(String key) {
		try {
			ServletContext servletContext = getServletContext();
			return servletContext.getAttribute(key);
		} catch (Exception e) {
		}
		return null;
	}
	
	
	/**
	 * 向ServletContext中设置参数
	 * @return
	 */
	public void setAttributeToServletContext(String key, Object obj) {
		try {
			ServletContext servletContext = getServletContext();
			servletContext.setAttribute(key, obj);
		} catch (Exception e) {
		}
	}
	
	/**
	 * 在Controller中获取HttpSession对象
	 * @return
	 */
	public HttpSession getSession() {
		HttpSession session = null;
		try {
			session = getRequest().getSession();
		} catch (Exception e) {
		}
		return session;
	}
	
	/**
	 * 从HttpSession中获取参数
	 * @return
	 */
	public Object getAttributeFromHttpSession(String key) {
		try {
			HttpSession session = getSession();
			return session.getAttribute(key);
		} catch (Exception e) {
		}
		return null;
	}
	
	
	/**
	 * 向HttpSession中设置参数
	 * @return
	 */
	public void setAttributeToHttpSession(String key, Object obj) {
		try {
			HttpSession session = getSession();
			session.setAttribute(key, obj);
		} catch (Exception e) {
		}
	}
	
	/**
	 * 在Controller中获取HttpRequest对象
	 * @return
	 */
	public HttpServletRequest getRequest() {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return attrs.getRequest();
	}
	
}
