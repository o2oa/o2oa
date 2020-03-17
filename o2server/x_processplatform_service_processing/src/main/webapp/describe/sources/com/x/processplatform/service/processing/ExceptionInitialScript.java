package com.x.processplatform.service.processing;

import com.x.base.core.project.exception.PromptException;

class ExceptionInitialScript extends PromptException {

	private static final long serialVersionUID = -2875973555673919197L;

	ExceptionInitialScript(Exception e, String text) {
		super(e, "初始化脚本执行错误, script:{}.", text);
	}
}
