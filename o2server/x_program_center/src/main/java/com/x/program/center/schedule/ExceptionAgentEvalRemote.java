package com.x.program.center.schedule;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAgentEvalRemote extends LanguagePromptException {

	private static final long serialVersionUID = -8597019540568284908L;

	ExceptionAgentEvalRemote(Throwable cause, String id, String name, String centerServer) {
		super(cause, "agent eval remote error, id:{}, name:{}, centerServer:{}, message:{}.", id, name, centerServer,
				cause.getMessage());
	}
}