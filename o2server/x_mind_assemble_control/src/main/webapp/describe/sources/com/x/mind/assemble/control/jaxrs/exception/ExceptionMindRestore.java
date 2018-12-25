package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindRestore extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindRestore( Throwable e, String id ) {
		super("脑图信息还原时发生异常！ID:" + id, e );
	}
}
