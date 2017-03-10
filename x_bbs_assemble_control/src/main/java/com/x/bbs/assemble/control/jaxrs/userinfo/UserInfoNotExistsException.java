package com.x.bbs.assemble.control.jaxrs.userinfo;

import com.x.base.core.exception.PromptException;

class UserInfoNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	UserInfoNotExistsException( String id ) {
		super("指定ID的BBS用户信息不存在.ID:" + id );
	}
}
