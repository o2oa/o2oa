package com.x.okr.assemble.control.jaxrs.workimport.exception;

import com.x.base.core.project.exception.PromptException;

public class GetOkrUserCacheException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public GetOkrUserCacheException( Throwable e, String userName, String proxyIdentity ) {
		super("根据员工和代理员工姓名获取OKR系统登录信息对象时发生异常.!用户:'" + userName +"'，代理者身份：'"+ proxyIdentity +"'.", e );
	}
	
	public GetOkrUserCacheException( Throwable e, String userName ) {
		super("根据员工姓名获取OKR系统登录信息对象时发生异常!用户:'" + userName +"'.", e );
	}
}
