package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import com.x.base.core.exception.PromptException;

class WorkCompleteDateLimitEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkCompleteDateLimitEmptyException() {
		super("工作完成时限为空，无法进行工作保存。" );
	}
}
