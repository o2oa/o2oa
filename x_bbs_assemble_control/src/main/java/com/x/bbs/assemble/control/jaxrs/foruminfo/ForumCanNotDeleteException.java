package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.exception.PromptException;

class ForumCanNotDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumCanNotDeleteException( String message ) {
		super( message );
	}
}
