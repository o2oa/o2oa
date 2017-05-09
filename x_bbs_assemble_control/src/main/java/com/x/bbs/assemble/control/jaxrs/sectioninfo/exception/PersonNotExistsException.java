package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.exception.PromptException;

public class PersonNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public PersonNotExistsException( String name ) {
		super("人员不存在.Name:" + name );
	}
}
