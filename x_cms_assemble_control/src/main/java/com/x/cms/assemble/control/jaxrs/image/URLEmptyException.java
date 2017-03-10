package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.exception.PromptException;

class URLEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	URLEmptyException() {
		super("URL为空，无法进行查询操作。" );
	}
}
