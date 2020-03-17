package com.x.program.center.jaxrs.agent;

import com.x.base.core.project.exception.PromptException;

class ExceptionAgentNotExist extends PromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionAgentNotExist(String flag) {
		super("代理: {} 不存在.", flag);
	}
}
