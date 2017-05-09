package com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.exception;

import com.x.base.core.exception.PromptException;

public class WorkIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkIdEmptyException() {
		super("工作ID为空。" );
	}
}
