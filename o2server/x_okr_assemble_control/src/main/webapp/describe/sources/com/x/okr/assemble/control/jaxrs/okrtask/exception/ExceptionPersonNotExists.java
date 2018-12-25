package com.x.okr.assemble.control.jaxrs.okrtask.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPersonNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPersonNotExists( String flag ) {
		super("用户信息不存在!Flag:" + flag );
	}
}
