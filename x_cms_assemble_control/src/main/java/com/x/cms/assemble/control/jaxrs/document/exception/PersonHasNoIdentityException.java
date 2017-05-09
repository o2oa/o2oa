package com.x.cms.assemble.control.jaxrs.document.exception;

import com.x.base.core.exception.PromptException;

public class PersonHasNoIdentityException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public PersonHasNoIdentityException( String name ) {
		super("系统未检测到用户身份信息，请检查用户的部门分配设置。Name:" + name );
	}
}
