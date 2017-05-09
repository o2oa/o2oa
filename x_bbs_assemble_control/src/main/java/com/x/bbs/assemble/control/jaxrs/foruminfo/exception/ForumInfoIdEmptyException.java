package com.x.bbs.assemble.control.jaxrs.foruminfo.exception;

import com.x.base.core.exception.PromptException;

public class ForumInfoIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ForumInfoIdEmptyException() {
		super("id为空， 无法进行查询." );
	}
}
