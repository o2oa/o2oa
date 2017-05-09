package com.x.cms.assemble.control.jaxrs.document.exception;

import com.x.base.core.exception.PromptException;

public class PersonIdentityQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public PersonIdentityQueryException( Throwable e, String name ) {
		super("系统在查询用户身份信息时发生异常。Name:" + name );
	}
}
