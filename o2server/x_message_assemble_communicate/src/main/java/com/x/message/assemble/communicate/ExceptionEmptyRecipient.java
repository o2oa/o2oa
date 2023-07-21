package com.x.message.assemble.communicate;

import com.x.base.core.project.exception.PromptException;

class ExceptionEmptyRecipient extends PromptException {

	private static final long serialVersionUID = 5966961923060058124L;

	ExceptionEmptyRecipient(String person) {
		super("can't find {} mail address.", person);
	}
}
