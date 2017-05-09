package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception;

import com.x.base.core.exception.PromptException;

public class CompanyWorkManagerCheckException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public CompanyWorkManagerCheckException( Throwable e, String person ) {
		super("检查用户是否公司管理员过程中发生异常。Person:" + person, e );
	}
}
