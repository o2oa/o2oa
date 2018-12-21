package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUserInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUserInfoNotExists( String id ) {
		super("指定ID的BBS用户信息不存在.ID:" + id );
	}
}
