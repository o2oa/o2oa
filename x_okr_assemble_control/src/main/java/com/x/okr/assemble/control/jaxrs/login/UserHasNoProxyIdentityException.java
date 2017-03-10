package com.x.okr.assemble.control.jaxrs.login;

import com.x.base.core.exception.PromptException;

class UserHasNoProxyIdentityException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	UserHasNoProxyIdentityException( String userName, String proxyIdentity ) {
		super("用户'" + userName +"'没有用户'"+ proxyIdentity +"'的代理身份，无法继续登录应用." );
	}
}
