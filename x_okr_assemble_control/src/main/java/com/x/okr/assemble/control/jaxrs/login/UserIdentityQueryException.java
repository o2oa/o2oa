package com.x.okr.assemble.control.jaxrs.login;

import com.x.base.core.exception.PromptException;

class UserIdentityQueryException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	UserIdentityQueryException( Throwable e, String userName ) {
		super("根据员工获取员工默认身份信息时发生异常.!用户:'" + userName +"'", e );
	}
	
	UserIdentityQueryException( Throwable e, String userName, String proxyIdentity ) {
		super("根据员工和代理员工姓名查询秘书配置信息时发生异常.!用户:'" + userName +"'，代理者身份：'"+ proxyIdentity +"'.", e );
	}
}
