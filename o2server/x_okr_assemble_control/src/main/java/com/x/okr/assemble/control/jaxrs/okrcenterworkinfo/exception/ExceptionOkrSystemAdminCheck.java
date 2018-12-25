package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionOkrSystemAdminCheck extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionOkrSystemAdminCheck( Throwable e, String userName ) {
		super("系统在判断用户是否是OKR系统管理员时发生异常.Name:" + userName, e );
	}
}
