package com.x.bbs.assemble.control.jaxrs.userinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionUserInfoQueryByName extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionUserInfoQueryByName( Throwable e, String name ) {
		super("根据指定姓名查询BBS用户信息时发生异常.Name:" + name, e );
	}
}
