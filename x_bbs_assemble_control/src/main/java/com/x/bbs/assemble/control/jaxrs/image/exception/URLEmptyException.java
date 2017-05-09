package com.x.bbs.assemble.control.jaxrs.image.exception;

import com.x.base.core.exception.PromptException;

public class URLEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public URLEmptyException() {
		super("URL为空!" );
	}

	public URLEmptyException( Throwable e, String url) {
		super("URL不合法! URL:" + url, e );
	}
}
