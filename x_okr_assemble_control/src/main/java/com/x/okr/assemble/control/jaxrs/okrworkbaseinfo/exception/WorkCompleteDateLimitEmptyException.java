package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.exception;

import com.x.base.core.exception.PromptException;

public class WorkCompleteDateLimitEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkCompleteDateLimitEmptyException() {
		super("工作完成时限为空，无法进行工作保存。" );
	}
}
