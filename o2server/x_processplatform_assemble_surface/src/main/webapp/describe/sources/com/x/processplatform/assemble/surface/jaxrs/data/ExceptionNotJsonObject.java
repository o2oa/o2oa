package com.x.processplatform.assemble.surface.jaxrs.data;

import com.x.base.core.project.exception.PromptException;

class ExceptionNotJsonObject extends PromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionNotJsonObject( ) {
		super("更新的数据不能为数组.");
	}
}
