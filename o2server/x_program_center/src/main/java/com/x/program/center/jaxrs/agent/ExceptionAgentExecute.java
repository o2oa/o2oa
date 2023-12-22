package com.x.program.center.jaxrs.agent;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAgentExecute extends LanguagePromptException {

	private static final long serialVersionUID = -8597019540568284908L;

	ExceptionAgentExecute(Throwable cause, String id, String name) {
		super(cause, "agent execute error, id:{}, name:{}, message:{}.", id, name, cause.getMessage());
	}
}