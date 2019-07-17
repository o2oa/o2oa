package com.x.processplatform.assemble.designer.jaxrs.projection;

import com.x.base.core.project.exception.PromptException;

class ExceptionDuplicateTaskCompleted extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionDuplicateTaskCompleted() {
		super("已办映射已存在.");
	}

}
