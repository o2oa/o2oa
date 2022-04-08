package com.x.message.assemble.communicate;

import com.x.base.core.project.exception.PromptException;

class ExceptionMessageRestfulItem extends PromptException {

	private static final long serialVersionUID = 5966961923060058124L;

	ExceptionMessageRestfulItem(String item) {
		super("can't find item:{} in message restful items.", item);
	}
}
