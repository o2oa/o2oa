package com.x.program.center.schedule;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAgentEvalLocal extends LanguagePromptException {

	private static final long serialVersionUID = -8597019540568284908L;

	ExceptionAgentEvalLocal(Throwable cause, String id, String name) {
		super(cause, "agent eval local error, id:{}, name:{}, message:{}.", id, name, cause.getMessage());
	}
}