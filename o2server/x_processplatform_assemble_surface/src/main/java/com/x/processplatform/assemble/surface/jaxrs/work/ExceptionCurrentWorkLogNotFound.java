package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.PromptException;

class ExceptionCurrentWorkLogNotFound extends PromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionCurrentWorkLogNotFound(String id) {
		super("无法找到工作:{}, 的当前工作日志.", id);
	}

}
