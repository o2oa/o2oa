package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.exception.PromptException;

public class UserInfoQueryByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public UserInfoQueryByIdException( Throwable e, String id ) {
		super("根据指定ID查询BBS用户信息时发生异常.ID:" + id, e );
	}
}
