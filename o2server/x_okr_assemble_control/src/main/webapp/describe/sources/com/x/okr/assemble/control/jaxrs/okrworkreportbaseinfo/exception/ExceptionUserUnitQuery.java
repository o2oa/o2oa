package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUserUnitQuery extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUserUnitQuery( Throwable e, String userName ) {
		super("系统通过操作用户查询用户身份和组织信息时发生异常!Person:'" + userName +"'.", e );
	}
}
