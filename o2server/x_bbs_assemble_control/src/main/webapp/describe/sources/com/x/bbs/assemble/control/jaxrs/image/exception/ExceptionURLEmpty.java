package com.x.bbs.assemble.control.jaxrs.image.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionURLEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionURLEmpty() {
		super("URL为空!" );
	}

	public ExceptionURLEmpty( Throwable e, String url) {
		super("URL不合法! URL:" + url, e );
	}
}
