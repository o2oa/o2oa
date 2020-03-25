package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSystemWorkManagerCheck extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSystemWorkManagerCheck( Throwable e, String person ) {
		super("检查用户是否顶层组织管理员过程中发生异常。Person:" + person, e );
	}
}
