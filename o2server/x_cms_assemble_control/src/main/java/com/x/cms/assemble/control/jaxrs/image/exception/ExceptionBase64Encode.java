package com.x.cms.assemble.control.jaxrs.image.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionBase64Encode extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionBase64Encode( Throwable e, String url ) {
		super("系统将图片转换为Base64编码发生异常。URL:" + url );
	}
}
