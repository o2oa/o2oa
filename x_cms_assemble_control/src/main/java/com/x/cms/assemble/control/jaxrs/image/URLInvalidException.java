package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.exception.PromptException;

class URLInvalidException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	URLInvalidException() {
		super("图片地址URL不合法,无法获取互联网图片信息。" );
	}
}
