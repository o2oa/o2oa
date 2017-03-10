package com.x.bbs.assemble.control.jaxrs.foruminfo;

import com.x.base.core.exception.PromptException;

class ForumNameEmptyException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ForumNameEmptyException() {
		super("论坛分区名称forumName为空， 无法进行查询." );
	}
}
