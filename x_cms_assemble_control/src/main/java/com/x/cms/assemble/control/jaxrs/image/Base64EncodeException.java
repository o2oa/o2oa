package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.exception.PromptException;

class Base64EncodeException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	Base64EncodeException( Throwable e, String url ) {
		super("系统将图片转换为Base64编码发生异常。URL:" + url );
	}
}
