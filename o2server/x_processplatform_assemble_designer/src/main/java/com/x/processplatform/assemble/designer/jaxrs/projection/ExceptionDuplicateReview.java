package com.x.processplatform.assemble.designer.jaxrs.projection;

import com.x.base.core.project.exception.PromptException;

class ExceptionDuplicateReview extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionDuplicateReview() {
		super("参阅映射已存在.");
	}

}
