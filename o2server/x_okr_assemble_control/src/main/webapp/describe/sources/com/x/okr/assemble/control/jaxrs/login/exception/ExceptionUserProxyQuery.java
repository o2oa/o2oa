package com.x.okr.assemble.control.jaxrs.login.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUserProxyQuery extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUserProxyQuery( Throwable e, String userName, String proxyIdentity ) {
		super("根据员工和代理员工姓名查询秘书配置信息时发生异常.!用户:'" + userName +"'，代理者身份：'"+ proxyIdentity +"'.", e );
	}
}
