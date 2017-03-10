package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.exception.PromptException;

class UserManagerCheckException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	UserManagerCheckException( Throwable e, String name ) {
		super("系统在检查用户是否是平台管理员时发生异常。Name:" + name );
	}
}
