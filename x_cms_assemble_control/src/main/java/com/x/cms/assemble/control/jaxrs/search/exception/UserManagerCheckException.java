package com.x.cms.assemble.control.jaxrs.search.exception;

import com.x.base.core.exception.PromptException;

public class UserManagerCheckException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public UserManagerCheckException( Throwable e, String name ) {
		super("系统在检查用户是否是平台管理员时发生异常。Name:" + name );
	}
}
