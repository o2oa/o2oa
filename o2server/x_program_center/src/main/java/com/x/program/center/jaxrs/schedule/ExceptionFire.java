package com.x.program.center.jaxrs.schedule;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFire extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionFire(Throwable throwable, String className, String application, String node) {
		super(throwable, "错误触发时间表:className:{}，应用程序:{}，节点:{}.", className, application,
				node);
	}
}
