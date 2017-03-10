package com.x.bbs.assemble.control.jaxrs.userinfo;

import com.x.base.core.exception.PromptException;

class UserInfoQueryByNameException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	UserInfoQueryByNameException( Throwable e, String name ) {
		super("根据指定姓名查询BBS用户信息时发生异常.Name:" + name, e );
	}
}
