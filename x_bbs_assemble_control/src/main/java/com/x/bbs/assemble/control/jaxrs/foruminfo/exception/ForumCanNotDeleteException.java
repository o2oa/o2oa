package com.x.bbs.assemble.control.jaxrs.foruminfo.exception;

import com.x.base.core.exception.PromptException;

public class ForumCanNotDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ForumCanNotDeleteException( String message ) {
		super( message );
	}
}
