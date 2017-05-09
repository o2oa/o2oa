package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.exception.PromptException;

public class UserInfoNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public UserInfoNotExistsException( String id ) {
		super("指定ID的BBS用户信息不存在.ID:" + id );
	}
}
