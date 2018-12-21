package com.x.okr.assemble.control.jaxrs.login.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionGetOkrUserCache extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionGetOkrUserCache( Throwable e, String userName, String proxyIdentity ) {
		super("根据员工和代理员工姓名获取OKR系统登录信息对象时发生异常.!用户:'" + userName +"'，代理者身份：'"+ proxyIdentity +"'.", e );
	}
	
	public ExceptionGetOkrUserCache( Throwable e, String userName ) {
		super("根据员工姓名获取OKR系统登录信息对象时发生异常!用户:'" + userName +"'.", e );
	}
}
