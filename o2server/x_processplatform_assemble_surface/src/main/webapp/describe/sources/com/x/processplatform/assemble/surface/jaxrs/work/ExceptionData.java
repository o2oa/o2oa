package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionData extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionData(Throwable throwable, String job) {
		super(throwable, "获取数据失败job:{}.", job);
	}

}
