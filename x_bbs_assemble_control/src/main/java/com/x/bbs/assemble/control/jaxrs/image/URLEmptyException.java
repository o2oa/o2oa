package com.x.bbs.assemble.control.jaxrs.image;

import com.x.base.core.exception.PromptException;

class URLEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	URLEmptyException() {
		super("URL为空!" );
	}

	URLEmptyException( Throwable e, String url) {
		super("URL不合法! URL:" + url, e );
	}
}
