package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionConversationCheckError extends PromptException {
	ExceptionConversationCheckError( String checkResult) {
		super("{}", checkResult);
	}
}
