package com.x.bbs.assemble.control.jaxrs.replyinfo.exception;

import com.x.base.core.exception.PromptException;

public class ReplyInfoProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ReplyInfoProcessException( Throwable e, String message ) {
		super( message, e );
	}
}
