package com.x.message.assemble.communicate;

import com.x.base.core.project.exception.PromptException;

class ExceptionMessageMqItem extends PromptException {

	private static final long serialVersionUID = 5966961923060058124L;

	ExceptionMessageMqItem(String item) {
		super("can't find item:{} in message mq items.", item);
	}
}
