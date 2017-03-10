package com.x.okr.assemble.control.jaxrs.okrauthorize;

import com.x.base.core.exception.PromptException;

class PersonNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	PersonNotExistsException( String name ) {
		super("人员不存在。Person:" + name );
	}
}
