package com.x.processplatform.core.express;

import com.x.base.core.project.exception.PromptException;

class ExceptionDataConvert extends PromptException {

	private static final long serialVersionUID = -2875973555673919197L;

	ExceptionDataConvert(Exception e, String job) {
		super(e, "无法将DataItem转换为Data对象, job:{}.", job);
	}
}
