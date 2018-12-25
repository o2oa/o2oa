package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionBindUserNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionBindUserNameEmpty() {
		super("绑定人员姓名为空， 无法进行查询." );
	}
}
