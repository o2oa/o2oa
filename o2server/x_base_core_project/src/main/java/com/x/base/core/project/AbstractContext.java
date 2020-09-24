package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
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

}
