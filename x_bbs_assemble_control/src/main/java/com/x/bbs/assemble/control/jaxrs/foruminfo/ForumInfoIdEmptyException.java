package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.exception.PromptException;

class ForumInfoIdEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumInfoIdEmptyException() {
		super("id为空， 无法进行查询." );
	}
}
