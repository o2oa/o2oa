package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUserInfoQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUserInfoQueryById( Throwable e, String id ) {
		super("根据指定ID查询BBS用户信息时发生异常.ID:" + id, e );
	}
}
