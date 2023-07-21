package com.x.base.core.project;

import java.util.Date;

import javax.servlet.ServletContext;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.schedule.AbstractJob;

public abstract class AbstractContext {

	// Applications资源
	protected Applications applications = new Applications();

	protected static final String INITPARAMETER_PORJECT = "project";

	public static final String SERVLETCONTEXT_ATTRIBUTE_APPLICATION = "application";

	// 应用类
	protected Class<?> clazz;

	public Class<?> clazz() {
		return this.clazz;
	}

	// 模块指示类
	protected Module module;

	public Module module() {
		return this.module;
	}

	// 从servletContext中抽取context
	public static AbstractContext fromServletContext(ServletContext servletContext) throws IllegalStateException {
		Object o = servletContext.getAttribute(AbstractContext.class.getName());
		if (null == o) {
			throw new IllegalStateException("can not get context form servletContext.");
		} else {
			return (AbstractContext) o;
		}
	}

	public abstract <T extends AbstractJob> void fireScheduleOnLocal(Class<T> cls, Integer delay) throws Exception;

	private Date applicationsTimestamp = null;

	public Applications applications() throws Exception {
		if ((null == this.applicationsTimestamp) || ((null != Config.resource_node_applicationsTimestamp())
				&& this.applicationsTimestamp.before(Config.resource_node_applicationsTimestamp()))) {
			JsonElement jsonElement = Config.resource_node_applications();
			if (null != jsonElement && (!jsonElement.isJsonNull())) {
				synchronized (this) {
					this.applications = XGsonBuilder.instance().fromJson(jsonElement, Applications.class);
					this.applicationsTimestamp = Config.resource_node_applicationsTimestamp();
				}
			}
		}
		return this.applications;
	}
}
