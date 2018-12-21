package com.x.okr.assemble.control.jaxrs.workimport.exception;

import com.x.base.core.project.exception.PromptException;

public class UserNoLoginException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public UserNoLoginException( String userName ) {
		super("用户未正常登录或者登录信息过期，请重新登录OKR系统!用户:'" + userName +"'." );
	}
}
