package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindWrapOutConvert extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindWrapOutConvert( Throwable e, String message ) {
		super("脑图信息转换时发生异常。MESSAGE:" + message, e );
	}
}
