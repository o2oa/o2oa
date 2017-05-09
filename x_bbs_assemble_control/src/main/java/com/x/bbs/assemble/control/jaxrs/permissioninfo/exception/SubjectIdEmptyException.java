package com.x.bbs.assemble.control.jaxrs.permissioninfo.exception;

import com.x.base.core.exception.PromptException;

public class SubjectIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public SubjectIdEmptyException() {
		super("主题ID为空， 无法进行查询." );
	}
}
