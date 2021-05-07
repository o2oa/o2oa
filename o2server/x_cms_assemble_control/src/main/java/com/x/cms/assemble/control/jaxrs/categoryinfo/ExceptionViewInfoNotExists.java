package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionViewInfoNotExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewInfoNotExists( String id ) {
		super("com.x.cms.assemble.control.jaxrs.comment.ExceptionCommentIdForQueryEmpty", id );
	}
}
