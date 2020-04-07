package com.x.query.assemble.designer.jaxrs.stat;

import com.x.base.core.project.exception.PromptException;

class ExceptionStatNotExist extends PromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionStatNotExist(String flag) {
		super("统计: {}, 不存在.", flag);
	}
}
