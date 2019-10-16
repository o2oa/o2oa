package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;

public abstract class AbstractContext {
	/** Applications资源 */
	protected volatile Applications applications;

	protected static final String INITPARAMETER_PORJECT = "project";

	public abstract Applications applications() throws Exception;

	protected static String getName(Class<?> clz) throws Exception {
		Module module = clz.getAnnotation(Module.class);
		return module.name();
	}

	/* 应用类 */
	protected Class<?> clazz;

	public Class<?> clazz() {
		return this.clazz;
	}

//	/* 应用类对象 */
//	protected Deployable clazzInstance;

	/* 模块指示类 */
	protected Module module;

	public Module module() {
		return this.module;
	}

//	public Deployable clazzInstance() {
//		return this.clazzInstance;
//	}

}
