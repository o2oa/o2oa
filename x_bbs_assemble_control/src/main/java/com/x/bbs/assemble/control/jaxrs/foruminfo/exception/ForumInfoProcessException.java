package com.x.bbs.assemble.control.jaxrs.foruminfo.exception;

import com.x.base.core.exception.PromptException;

public class ForumInfoProcessException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ForumInfoProcessException( Throwable e, String message ) {
		super( message, e );
	}
}
