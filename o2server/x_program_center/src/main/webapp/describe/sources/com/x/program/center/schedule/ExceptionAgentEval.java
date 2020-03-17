package com.x.program.center.schedule;

import com.x.base.core.project.exception.PromptException;

class ExceptionAgentEval extends PromptException {

	private static final long serialVersionUID = -8597019540568284908L;

	ExceptionAgentEval(Throwable cause, String message, String id, String name, String alias, String text) {
		super(cause, "agent id:{}, name:{}, alias:{}, eval error: {}, script:{}.", id, name, alias, message, text);
	}
}