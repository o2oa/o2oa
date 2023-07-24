package com.x.bbs.assemble.control.jaxrs.foruminfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionForumCanNotDelete extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionForumCanNotDelete( String message ) {
		super( message );
	}
}
