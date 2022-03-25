package com.x.program.center.jaxrs.code;

import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionTransferCodeError extends LanguagePromptException {

	private static final long serialVersionUID = -3287459468603291619L;

	ExceptionTransferCodeError(ActionResponse resp) {
		super("transfer code message error: {}.", resp);
	}
}
