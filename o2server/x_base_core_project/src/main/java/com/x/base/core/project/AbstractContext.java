package com.x.base.core.project;

import javax.servlet.ServletContext;

import org.quartz.DateBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.DateBuilder.IntervalUnit;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.thread.ThreadFactory;

public abstract class AbstractContext {

	// Applications资源
	protected volatile Applications applications;

	protected static final String INITPARAMETER_PORJECT = "project";

	public abstract Applications applications() throws Exception;

	protected ThreadFactory threadFactory;

	public abstract ThreadFactory threadFactory();

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
}
