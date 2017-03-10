package com.x.bbs.assemble.control.jaxrs.roleinfo;

import com.x.base.core.exception.PromptException;

class BindUserNameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	BindUserNameEmptyException() {
		super("绑定人员姓名为空， 无法进行查询." );
	}
}
