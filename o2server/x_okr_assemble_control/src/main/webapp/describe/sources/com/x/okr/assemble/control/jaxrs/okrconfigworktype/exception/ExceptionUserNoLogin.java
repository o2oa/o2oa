package com.x.okr.assemble.control.jaxrs.okrconfigworktype.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUserNoLogin extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUserNoLogin( String userName ) {
		super("用户未正常登录或者登录信息过期，请重新登录OKR系统!用户:'" + userName +"'." );
	}
}
