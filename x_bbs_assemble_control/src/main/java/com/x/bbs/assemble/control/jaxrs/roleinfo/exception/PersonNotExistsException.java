package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.exception.PromptException;

public class PersonNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public PersonNotExistsException( String person ) {
		super("人员信息不存在！Person:" + person );
	}
}
