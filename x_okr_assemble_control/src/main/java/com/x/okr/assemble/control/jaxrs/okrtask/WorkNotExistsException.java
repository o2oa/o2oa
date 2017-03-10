package com.x.okr.assemble.control.jaxrs.okrtask;

import com.x.base.core.exception.PromptException;

class WorkNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	WorkNotExistsException( String id ) {
		super("具体工作信息不存在!ID:" + id );
	}
}
