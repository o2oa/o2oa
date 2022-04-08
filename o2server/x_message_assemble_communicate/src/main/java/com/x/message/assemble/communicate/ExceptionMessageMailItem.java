package com.x.message.assemble.communicate;

import com.x.base.core.project.exception.PromptException;

class ExceptionMessageMailItem extends PromptException {

	private static final long serialVersionUID = 5966961923060058124L;

	ExceptionMessageMailItem(String item) {
		super("can't find item:{} in message mail items.", item);
	}
}
