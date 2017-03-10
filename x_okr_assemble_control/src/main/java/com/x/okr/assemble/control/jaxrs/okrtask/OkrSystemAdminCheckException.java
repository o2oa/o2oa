package com.x.okr.assemble.control.jaxrs.okrtask;

import com.x.base.core.exception.PromptException;

class OkrSystemAdminCheckException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	OkrSystemAdminCheckException( Throwable e, String userName ) {
		super("系统在判断用户是否是OKR系统管理员时发生异常.Name:" + userName, e );
	}
}
