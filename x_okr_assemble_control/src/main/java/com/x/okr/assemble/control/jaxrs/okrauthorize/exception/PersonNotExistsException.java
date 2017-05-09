package com.x.okr.assemble.control.jaxrs.okrauthorize.exception;

import com.x.base.core.exception.PromptException;

public class PersonNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public PersonNotExistsException( String name ) {
		super("人员不存在。Person:" + name );
	}
}
