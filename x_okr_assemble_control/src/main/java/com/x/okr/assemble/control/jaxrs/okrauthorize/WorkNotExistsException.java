package com.x.okr.assemble.control.jaxrs.okrauthorize;

import com.x.base.core.exception.PromptException;

class WorkNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkNotExistsException( String id ) {
		super("工作不存在。Id:" + id );
	}
}
