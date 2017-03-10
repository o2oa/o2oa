package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.exception.PromptException;

class DataAlreadyExistedException extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	DataAlreadyExistedException(String title, String workId) {
		super("work title:{} id:{}, already has data.", title, workId);
	}
}
