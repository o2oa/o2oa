package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;

public abstract class AbstractContext {
	/** Applications资源 */
	protected volatile Applications applications;

	protected static final String INITPARAMETER_PORJECT = "project";

	public Applications applications() {
		synchronized (this) {
			return this.applications;
		}
	}

	protected static String getName(Class<?> clz) throws Exception {
		Module module = clz.getAnnotation(Module.class);
		return module.name();
	}

}
