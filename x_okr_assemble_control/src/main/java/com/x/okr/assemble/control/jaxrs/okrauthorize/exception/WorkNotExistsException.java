package com.x.okr.assemble.control.jaxrs.okrauthorize.exception;

import com.x.base.core.exception.PromptException;

public class WorkNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public WorkNotExistsException( String id ) {
		super("工作不存在。Id:" + id );
	}
}
